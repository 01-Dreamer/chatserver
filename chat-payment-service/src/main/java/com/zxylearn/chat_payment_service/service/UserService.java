package com.zxylearn.chat_payment_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxylearn.chat_payment_service.pojo.entity.User;

public interface UserService extends IService<User> {
    boolean addUser(User user);
    User getUserByUsername(String username);
}