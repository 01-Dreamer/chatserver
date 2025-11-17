package com.zxylearn.chat_session_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxylearn.chat_session_service.pojo.entity.Session;

public interface SessionService extends IService<Session> {
    public boolean areFriends(Long userId1, Long userId2);
    public boolean addSession(Session session);
}