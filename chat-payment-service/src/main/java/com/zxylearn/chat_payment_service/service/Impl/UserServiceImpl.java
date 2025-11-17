package com.zxylearn.chat_payment_service.service.Impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxylearn.chat_payment_service.mapper.UserMapper;
import com.zxylearn.chat_payment_service.pojo.entity.User;
import com.zxylearn.chat_payment_service.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public boolean addUser(User user) {
        if(user == null) {
            return false;
        }
        return baseMapper.insert(user) > 0;
    }

    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return baseMapper.selectOne(wrapper);
    }
}