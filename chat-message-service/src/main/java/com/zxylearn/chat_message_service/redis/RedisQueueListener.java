package com.zxylearn.chat_message_service.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxylearn.chat_message_service.hutool.IdGeneratorService;
import com.zxylearn.chat_message_service.pojo.entity.Message;
import com.zxylearn.chat_message_service.service.MessageService;
import com.zxylearn.chat_message_service.service.SessionMemberService;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class RedisQueueListener {

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

    private static final List<String> SUPPORTED_MESSAGE_TYPES = List.of("text", "image", "file", "red_packet");

    @Async
    public void msgProcessListening() {
        while (true) {
            try {
                String msgFromRedis = redisService.popMessageFromProcessQueue();
                if(msgFromRedis == null) {
                    continue;
                }

                int sepPos = msgFromRedis.lastIndexOf("@");
                String jsonMsg = msgFromRedis.substring(0, sepPos);
                String senderIdFromWS = msgFromRedis.substring(sepPos + 1);
                try {
                    Message message = objectMapper.readValue(jsonMsg, Message.class);
                    String senderId = message.getSenderId().toString();
                    String sessionId = message.getSessionId().toString();

                    // 判断senderId是否和WebSocket传过来的一致
                    if(!senderIdFromWS.equals(senderId)) {
                        continue;
                    }

                    // 判断用户是否属于该Session
                    if(!isUserInSession(senderId, sessionId)) {
                        continue;
                    }

                    // 判断消息类型是否支持
                    if(!SUPPORTED_MESSAGE_TYPES.contains(message.getType())) {
                        continue;
                    }

                    // 消息处理[需要特殊处理红包信息]
                    message.setId(idGeneratorService.getId());
                    message.setCreateTime(new Date());
                    if(message.getType().equals("red_packet")) {
                        String amount = message.getContent();
                        if(amount == null || Long.parseLong(amount) <= 0) {
                            continue;
                        }
                        // TODO 还需处理扣钱逻辑
                        redisService.addRedPacket(message.getId().toString(), sessionId, senderId, amount);
                    }

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