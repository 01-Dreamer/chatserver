package com.zxylearn.chat_message_service.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxylearn.chat_message_service.pojo.entity.SessionMember;

public interface SessionMemberService extends IService<SessionMember> {
    public boolean isUserInSession(Long userId, Long sessionId);
    public List<Long> getUserIdsBySessionId(Long sessionId);
}
