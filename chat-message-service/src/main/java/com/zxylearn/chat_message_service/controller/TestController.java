package com.zxylearn.chat_message_service.controller;


import com.zxylearn.chat_message_service.redis.RedisMessageReceiver;
import com.zxylearn.chat_message_service.redis.RedisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // 注入"监听容器" (来自 RedisPubSubConfig)
    @Autowired
    private RedisMessageListenerContainer redisContainer;

    // 注入"消息处理器" (我们的 RedisMessageReceiver)
    @Autowired
    private RedisMessageReceiver messageReceiver;

    /**
     * 提供一个 API 来动态添加订阅
     * 比如： http://localhost:8080/subscribe/news
     * 比如： http://localhost:10001/subscribe/weather
     */
    @GetMapping("/subscribe/{channelName}")
    public String subscribe(@PathVariable String channelName) {

        // 1. 创建一个新的频道主题
        ChannelTopic topic = new ChannelTopic(channelName);
        
        // 2. 关键：将你的"消息处理器"添加到"监听容器"中，并指定监听的主题
        redisContainer.addMessageListener(messageReceiver, topic);
        
        String response = "成功订阅频道 (Subscribed to channel): " + channelName;
        System.out.println(response); // 也在控制台打印一下
        return response;
    }

    @Autowired
    private RedisService redisService;
    
    // ... (你已有的 /subscribe/{channelName} API) ...

    /**
     * ⭐️ 新增：一个用于测试发布的 API
     * 比如： http://localhost:10001/publish/test1?msg=Hello
     */
    @GetMapping("/publish/{channelName}")
    public String publishMessage(@PathVariable String channelName, @RequestParam String msg) {
        
        // 直接调用你封装好的 publish 方法
        redisService.publish(channelName, msg);
        
        String response = "消息已发送 (Message sent) to '" + channelName + "': " + msg;
        System.out.println(response);
        return response;
    }
}