package com.zxylearn.chat_message_service.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxylearn.chat_message_service.mapper.SessionMemberMapper;
import com.zxylearn.chat_message_service.pojo.entity.SessionMember;
import com.zxylearn.chat_message_service.service.SessionMemberService;

@Service
public class SessionMemberServiceImpl extends ServiceImpl<SessionMemberMapper, SessionMember> implements SessionMemberService {
    @Override
    public boolean isUserInSession(Long userId, Long sessionId) {
        if(userId == null || sessionId == null) {
            return false;
        }
        return this.lambdaQuery()
                    .eq(SessionMember::getUserId, userId)
                    .eq(SessionMember::getSessionId, sessionId)
                    .count() > 0;
    }

    @Override
    public List<Long> getUserIdsBySessionId(Long sessionId) {
        if(sessionId == null) {
            return null;
        }
        return this.lambdaQuery()
                   .eq(SessionMember::getSessionId, sessionId)
                   .select(SessionMember::getUserId)
                   .list()
                   .stream()
                   .map(SessionMember::getUserId)
                   .collect(Collectors.toList());
    }
}
