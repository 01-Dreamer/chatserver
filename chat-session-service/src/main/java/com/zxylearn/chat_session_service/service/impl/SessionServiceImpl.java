package com.zxylearn.chat_session_service.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxylearn.chat_session_service.mapper.SessionMapper;
import com.zxylearn.chat_session_service.pojo.entity.Session;
import com.zxylearn.chat_session_service.service.SessionService;

@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session> implements SessionService {
    @Override
    public Session getSession(Long sessionId) {
        return baseMapper.selectById(sessionId);
    }

    @Override
    public boolean areFriends(Long userId1, Long userId2) {
        QueryWrapper<Session> queryWrapper = new QueryWrapper<>();
        queryWrapper.nested(qw -> qw
                .eq("creater_id", userId1)
                .eq("peer_id", userId2)
        );
        queryWrapper.or();
        queryWrapper.nested(qw -> qw
                .eq("creater_id", userId2)
                .eq("peer_id", userId1)
        );
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean addSession(Session session) {
        if(session == null) {
            return false;
        }
        return baseMapper.insert(session) > 0;
    }

    @Override
    public boolean updateSessionName(Long sessionId, String newName) {
        Session session = baseMapper.selectById(sessionId);
        if (session == null) {
            return false;
        }
        session.setName(newName);
        return baseMapper.updateById(session) > 0;
    }

    @Override
    public String updateSessionAvatar(Long sessionId, String newAvatarUrl) {
        Session session = baseMapper.selectById(sessionId);
        if (session == null) {
            return null;
        }
        String oldAvatarUrl = session.getAvatar();
        session.setAvatar(newAvatarUrl);
        baseMapper.updateById(session);
        return oldAvatarUrl;
    }
}
