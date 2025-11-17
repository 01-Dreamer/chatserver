package com.zxylearn.chat_session_service.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxylearn.chat_session_service.mapper.SessionMemberMapper;
import com.zxylearn.chat_session_service.pojo.entity.SessionMember;
import com.zxylearn.chat_session_service.service.SessionMemberService;

@Service
public class SessionMemberServiceImpl extends ServiceImpl<SessionMemberMapper, SessionMember> implements SessionMemberService {
    @Override
    public boolean isUserInSession(Long userId, Long sessionId) {
        QueryWrapper<SessionMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("session_id", sessionId);
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean addSessionMember(SessionMember sessionMember) {
        if(sessionMember == null) {
            return false;
        }
        return baseMapper.insert(sessionMember) > 0;
    }
}