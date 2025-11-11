package com.zxylearn.chat_message_service.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxylearn.chat_message_service.hutool.IdGeneratorService;
import com.zxylearn.chat_message_service.pojo.entity.Message;
import com.zxylearn.chat_message_service.service.MessageService;
import com.zxylearn.chat_message_service.service.SessionMemberService;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisQueueListener {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SessionMemberService sessionMemberService;

    @Autowired
	private IdGeneratorService idGeneratorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageService messageService;

    private static final String MSG_PROCESS_QUEUE = "msg-process-queue";

    @Async
    public void msgProcessListening() {
        while (true) {
            try {
                String jsonMsg = stringRedisTemplate.opsForList().leftPop(MSG_PROCESS_QUEUE, 0, TimeUnit.SECONDS);
                if(jsonMsg == null) {
                    continue;
                }
                try {
                    Message message = objectMapper.readValue(jsonMsg, Message.class);
                    String senderId = message.getSenderId().toString();
                    String sessionId = message.getSessionId().toString();

                    // 判断用户是否属于该Session
                    if(!isUserInSession(senderId, sessionId)) {
                        continue;
                    }

                    // 消息处理
                    // TODO [后续需要特殊判断文件、图片、红包消息]
                    message.setId(idGeneratorService.getId());
                    message.setCreateTime(new Date());

                    // 推送消息
                    String processedMessage = objectMapper.writeValueAsString(message);
                    List<String> userIds = getUserIdsBySessionId(sessionId);
                    for(String userId: userIds) {
                        // 判断用户是否在线
                        if(redisService.isUserOnline(userId)) {
                            redisService.publish(userId, processedMessage);
                        } else {
                            redisService.addUnreadMessage(userId, message.getId().toString());
                        }
                    }

                    // 异步保存消息
                    messageService.saveMessageAsync(message);
                } catch (Exception e) {
                    log.error("failed to parse message: {}", e.getMessage());
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private boolean isUserInSession(String userId, String sessionId) {
        String key = "session:" + sessionId + ":members";

        // 缓存命中
        if(redisService.exist(key)) {
            return redisService.isUserInSession(userId, sessionId);
        }

        // 缓存未命中，查询数据库, 并更新缓存
        List<Long> userIds = sessionMemberService.getUserIdsBySessionId(Long.parseLong(sessionId));
        redisService.addSessionMembers(sessionId, userIds);
        return redisService.isUserInSession(userId, sessionId);
    }

    private List<String> getUserIdsBySessionId(String sessionId) {
        String key = "session:" + sessionId + ":members";

        // 缓存命中
        if(redisService.exist(key)) {
            return redisService.getUserIdsBySessionId(sessionId);
        }

        // 缓存未命中，查询数据库, 并更新缓存
        List<Long> userIds = sessionMemberService.getUserIdsBySessionId(Long.parseLong(sessionId));
        redisService.addSessionMembers(sessionId, userIds);

        return redisService.getUserIdsBySessionId(sessionId);
    }
}