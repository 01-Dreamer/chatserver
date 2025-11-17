package com.zxylearn.chat_session_service.redis;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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

    // 发布消息
    public boolean publish(String channel, String message) {
        if (channel == null || message == null) {
            return false;
        }
        stringRedisTemplate.convertAndSend(channel, message);
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

    // 修改session:members缓存
    public boolean updateSessionMembers(String sessionId, String userId, boolean isAdd) {
        if(sessionId == null || userId == null) {
            return false;
        }
        
        String key = "session:members:" + sessionId;
        if(!exist(key)) {
            return false;
        }

        if(isAdd) {
            stringRedisTemplate.opsForSet().add(key, userId);
        } else {
            stringRedisTemplate.opsForSet().remove(key, userId);
        }
        return true;
    }

}