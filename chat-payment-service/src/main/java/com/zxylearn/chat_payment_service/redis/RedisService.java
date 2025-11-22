package com.zxylearn.chat_payment_service.redis;

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

    // 用户在线状态
    public boolean isUserOnline(String userId) {
        if (userId == null) {
            return false;
        }
        return get("online:" + userId) != null;
    }

    // 添加未读消息
    public boolean addUnreadMessage(String userId, String messageId) {
        if(userId == null || messageId == null) {
            return false;
        }
        String key = "unread_message:" + userId;
        stringRedisTemplate.opsForSet().add(key, messageId);
        return true;
    }

    // 设置红包缓存
    public boolean setRedPacket(String redPacketId, String userId, String amount) {
        if(redPacketId == null || amount == null) {
            return false;
        }
        String key1 = "red_packet:" + redPacketId;
        String key2 = "red_packet_owner:" + redPacketId;

        if(exist(key1)) {
            return false;
        }

        set(key1, userId + ":" + amount, 86400);
        set(key2, userId + ":" + amount, 90000);
        return true;
    }

    // 获取红包缓存并删除
    public String getAndDeleteRedPacket(String redPacketId) {
        if(redPacketId == null) {
            return null;
        }
        String key1 = "red_packet:" + redPacketId;
        String key2 = "red_packet_owner:" + redPacketId;
        String value = get(key1);
        del(key1);
        del(key2);
        return value;
    }
}