package com.zxylearn.chat_session_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxylearn.chat_session_service.pojo.entity.Session;

public interface SessionService extends IService<Session> {
    public Session getSession(Long sessionId);
    public boolean areFriends(Long userId1, Long userId2);
    public boolean addSession(Session session);
    public boolean updateSessionName(Long sessionId, String newName);
    public String updateSessionAvatar(Long sessionId, String newAvatarUrl);
}