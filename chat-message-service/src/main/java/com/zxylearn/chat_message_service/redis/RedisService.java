package com.zxylearn.chat_message_service.redis;


import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;


@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Autowired
    private RedisMessageSubscribe redisMessageSubscribe;

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

    public boolean rpush(String key, String value) {
        if (key == null || value == null) {
            return false;
        }
        stringRedisTemplate.opsForList().rightPush(key , value);
        return true;
    }

    public String lpop(String key) {
        return key == null ? null : stringRedisTemplate.opsForList().leftPop(key);
    }

    public Boolean updateExpire(String key, long timeout) {
        if (key == null) {
            return false;
        }
        stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        return true;
    }

    // 添加订阅
    public boolean subscribe(String channelName) {
        if(channelName == null) {
            return false;
        }
        ChannelTopic topic = new ChannelTopic(channelName);
        redisMessageListenerContainer.addMessageListener(redisMessageSubscribe, topic);
        return true;
    }

    // 取消订阅
    public boolean unsubscribe(String channelName) {
        if(channelName == null) {
            return false;
        }
        ChannelTopic topic = new ChannelTopic(channelName);
        redisMessageListenerContainer.removeMessageListener(redisMessageSubscribe, topic);
        return true;
    }

    // 发布消息
    public boolean publish(String channel, String message) {
        if (channel == null || message == null) {
            return false;
        }
        stringRedisTemplate.convertAndSend(channel, message);
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
    public boolean addOnlineUser(String userId, String userInfo) {
        if (userId == null || userInfo == null) {
            return false;
        }
        if(isUserOnline(userId)) {
            return false;
        }
        set("online:" + userId, userInfo);
        subscribe(userId);
        return true;
    }

    // 删除在线用户
    public boolean removeOnlineUser(String userId) {
        if (userId == null) {
            return false;
        }
        del("online:" + userId);
        unsubscribe(userId);
        return true;
    }
    
    // 添加session:members缓存
    public boolean addSessionMembers(String sessionId, List<Long> userIds) {
        if (sessionId == null || userIds == null || userIds.isEmpty()) {
            return false;
        }
        String key = "session:members:" + sessionId;
        String[] members = userIds.stream()
                                  .map(String::valueOf)
                                  .toArray(String[]::new);
        stringRedisTemplate.opsForSet().add(key, members);
        updateExpire(key, 3600);
        return true;
    }

    // 获取session:members缓存
    public List<String> getSessionMembers(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        String key = "session:members:" + sessionId;
        return stringRedisTemplate.opsForSet().members(key).stream().toList();
    }

    // user是否在session中
    public boolean isUserInSession(String userId, String sessionId) {
        if (userId == null || sessionId == null) {
            return false;
        }
        String key = "session:members:" + sessionId;
        return stringRedisTemplate.opsForSet().isMember(key, String.valueOf(userId));
    }
}