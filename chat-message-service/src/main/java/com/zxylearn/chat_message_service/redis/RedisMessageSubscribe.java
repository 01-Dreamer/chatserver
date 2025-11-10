package com.zxylearn.chat_message_service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.zxylearn.chat_message_service.websocket.WebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisMessageSubscribe implements MessageListener {

    @Autowired
    @Lazy
    private WebSocketHandler webSocketHandler;

    private final StringRedisTemplate stringRedisTemplate;

    public RedisMessageSubscribe(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String userId = stringRedisTemplate.getStringSerializer().deserialize(message.getChannel());
        String msg = stringRedisTemplate.getStringSerializer().deserialize(message.getBody());
        try {
            webSocketHandler.sendMessageToUser(userId, msg);
        } catch (Exception e) {
            log.error("failed to send message to user {}: {}", userId, e.getMessage());
        }
    }
}