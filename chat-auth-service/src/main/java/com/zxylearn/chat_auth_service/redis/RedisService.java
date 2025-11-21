package com.zxylearn.chat_auth_service.redis;


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

    // 为APP添加用户信息缓存
    public boolean addUserInfoForApp(String uuid, String userInfo) {
        if (uuid == null || userInfo == null) {
            return false;
        }

        String key = "user_info:" + uuid;
        return set(key, userInfo, 60);
    }

    // APP获取缓存信息并删除
    public String getandDeleteUserInfoForApp(String uuid) {
        if (uuid == null) {
            return null;
        }
        String key = "user_info:" + uuid;
        String value = get(key);
        del(key);
        return value;
    }
    
    // 用户在线状态
    public boolean isUserOnline(String userId) {
        if (userId == null) {
            return false;
        }
        return get("online:" + userId) != null;
    }
}