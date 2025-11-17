package com.zxylearn.chat_session_service.redis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private DateTimeFormatter formatterOfTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    // 申请添加好友缓存
    public boolean addFriendApplication(String applicantId, String requesterId) {
        if(applicantId == null || requesterId == null) {
            return false;
        }
        String key = "friend_application:" + applicantId + ":" + requesterId;
        return set(key, LocalDateTime.now().format(formatterOfTime), 604800); // 缓存24h
    }

    // 检查好友申请缓存
    public boolean isFriendApplication(String applicantId, String requesterId) {
        String key = "friend_application:" + applicantId + ":" + requesterId;
        return exist(key);
    }

    // 删除好友申请缓存
    public boolean delFriendApplication(String userId1, String userId2) {
        if(userId1 == null || userId2 == null) {
            return false;
        }
        String key1 = "friend_application:" + userId1 + ":" + userId2;
        String key2 = "friend_application:" + userId2 + ":" + userId1;
        return del(key1)||del(key2);
    }

    // 申请添加群聊缓存
    public boolean addGroupApplication(String applicantId, String sessionId, String createrId) {
        if(applicantId == null || sessionId == null || createrId == null) {
            return false;
        }
        String key = "group_applicant:" + applicantId + ":" + sessionId + ":" + createrId;
        return set(key, LocalDateTime.now().format(formatterOfTime), 604800); // 缓存24h
    }
}