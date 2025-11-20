package com.zxylearn.chat_session_service.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxylearn.chat_session_service.mapper.UserMapper;
import com.zxylearn.chat_session_service.pojo.entity.User;
import com.zxylearn.chat_session_service.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public boolean isUserExist(Long userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId); 
        return baseMapper.exists(queryWrapper);
    }

    @Override
    public User getUser(Long userId) {
        return baseMapper.selectById(userId);
    }

    @Override
    public boolean updateUserNickname(Long userId, String newNickname) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        user.setNickname(newNickname);
        return baseMapper.updateById(user) > 0;
    }

    @Override
    public String updateUserAvatar(Long userId, String newAvatar) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        String oldAvatar = user.getAvatar();
        user.setAvatar(newAvatar);
        baseMapper.updateById(user);
        return oldAvatar;
    }
}