package com.zxylearn.chat_message_service.redis;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 统一的 Redis 操作服务类
 * (精简版：目前只提供发布功能)
 */
@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 向指定频道发布一条消息
     * @param channel 频道名称 (例如: "chat_messages", "user_updates")
     * @param message 消息内容 (字符串)
     */
    public void publish(String channel, String message) {
        if (channel == null || message == null) {
            // 在实际项目中，你可能想加一个日志
            System.err.println("Publish failed: Channel or message is null.");
            return;
        }
        stringRedisTemplate.convertAndSend(channel, message);
    }
}