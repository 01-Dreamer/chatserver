package com.zxylearn.chat_message_service.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息处理器，实现了 MessageListener 接口
 */
@Component
public class RedisMessageReceiver implements MessageListener {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 注入 StringRedisTemplate 来辅助反序列化
     * 因为 Redis 原始消息是 byte[]，我们需要 String 序列化器
     */
    public RedisMessageReceiver(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 这是当消息到达时，Spring 会自动调用的方法
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 使用 StringRedisTemplate 的序列化器将 byte[] 转换为 String
        String channel = stringRedisTemplate.getStringSerializer().deserialize(message.getChannel());
        String body = stringRedisTemplate.getStringSerializer().deserialize(message.getBody());

        // 你的 sout 逻辑
        System.out.println("====================================");
        System.out.println("收到消息 (Received Message): " + body);
        System.out.println("来自频道 (From Channel): " + channel);
        System.out.println("====================================");
    }
}