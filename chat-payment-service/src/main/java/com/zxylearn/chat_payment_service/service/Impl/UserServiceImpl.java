package com.zxylearn.chat_payment_service.service.Impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxylearn.chat_payment_service.mapper.UserMapper;
import com.zxylearn.chat_payment_service.pojo.entity.User;
import com.zxylearn.chat_payment_service.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public boolean isUserExists(Long userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean increaseBalance(Long userId, Long amount) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(User::getId, userId);
        updateWrapper.setSql("balance = balance + " + amount);
        return baseMapper.update(null, updateWrapper) > 0;
    }

    @Override
    public boolean descreaseBalance(Long userId, Long amount) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(User::getId, userId)
                .ge(User::getBalance, amount);
        updateWrapper.setSql("balance = balance - " + amount);
        return baseMapper.update(null, updateWrapper) > 0;
    }
}