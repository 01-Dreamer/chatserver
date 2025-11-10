package com.zxylearn.chat_message_service.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxylearn.chat_message_service.pojo.entity.Message;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisQueueListener {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ObjectMapper objectMapper;

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
                    message.setCreateTime(new Date());
                    System.out.println(message.toString());
                    System.out.println(objectMapper.writeValueAsString(message));
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
}