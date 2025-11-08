package com.zxylearn.chat_message_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisPubSubConfig {

    /**
     * 注册 Redis 消息监听容器
     * 这个 Bean 会在项目启动时自动创建，并开始监听 Redis 连接
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        
        // 注意：我们在这里 *不需要* 添加任何监听器
        // 监听器 (RedisMessageReceiver) 将在 Controller 中被动态添加
        
        return container;
    }
}