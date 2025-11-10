package com.zxylearn.chat_message_service.redis;


import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;


@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisMessageListenerContainer redisContainer;

    @Autowired
    private RedisMessageReceiver messageReceiver;

    public boolean publish(String channel, String message) {
        if (channel == null || message == null) {
            return false;
        }
        stringRedisTemplate.convertAndSend(channel, message);
        return true;
    }

    public boolean set(String key, String value) {
        if (key == null || value == null) {
            return false;
        }
        stringRedisTemplate.opsForValue().set(key, value);
        return true;
    }

    public boolean set(String key, String value, long timeout) {
        if (key == null || value == null) {
            return false;
        }
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        return true;
    }

    public String get(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
    }

    public Boolean exist(String key) {
        return key == null ? false : stringRedisTemplate.hasKey(key);
    }

    public Boolean del(String key) {
        return key == null ? false : stringRedisTemplate.delete(key);
    }

    // 统一为rpush
    public boolean push(String key, String value) {
        if (key == null || value == null) {
            return false;
        }
        stringRedisTemplate.opsForList().rightPush(key , value);
        return true;
    }

    // 统一为lpop
    public String pop(String key) {
        return key == null ? null : stringRedisTemplate.opsForList().leftPop(key);
    }

    // 添加订阅
    public boolean subscribe(String channelName) {
        if(channelName == null) {
            return false;
        }
        ChannelTopic topic = new ChannelTopic(channelName);
        redisContainer.addMessageListener(messageReceiver, topic);
        return true;
    }

    // 取消订阅
    public boolean unsubscribe(String channelName) {
        if(channelName == null) {
            return false;
        }
        ChannelTopic topic = new ChannelTopic(channelName);
        redisContainer.removeMessageListener(messageReceiver, topic);
        return true;
    }

    // 用户在线状态
    public boolean isUserOnline(String userId) {
        if (userId == null) {
            return false;
        }
        return get("online:" + userId) != null;
    }

    // 添加在线用户
    public boolean addOnlineUser(String userId, String sessionId) {
        if (userId == null || sessionId == null) {
            return false;
        }
        if(isUserOnline(userId)) {
            return false;
        }
        set("online:" + userId, sessionId);
        subscribe("message:" + userId);
        return true;
    }

    // 删除在线用户
    public boolean removeOnlineUser(String userId) {
        if (userId == null) {
            return false;
        }
        del("online:" + userId);
        unsubscribe("message:" + userId);
        return true;
    }
}