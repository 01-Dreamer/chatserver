package com.zxylearn.chat_payment_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxylearn.chat_payment_service.pojo.entity.User;

public interface UserService extends IService<User> {
    boolean isUserExists(Long userId);
    boolean increaseBalance(Long userId, Long amount);
    boolean descreaseBalance(Long userId, Long amount);
}