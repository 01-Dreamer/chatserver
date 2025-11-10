package com.zxylearn.chat_message_service.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


@Component
public class RedisMessageReceiver implements MessageListener {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisMessageReceiver(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = stringRedisTemplate.getStringSerializer().deserialize(message.getChannel());
        String body = stringRedisTemplate.getStringSerializer().deserialize(message.getBody());

        System.out.println("====================================");
        System.out.println("收到消息 (Received Message): " + body);
        System.out.println("来自频道 (From Channel): " + channel);
        System.out.println("====================================");
    }
}