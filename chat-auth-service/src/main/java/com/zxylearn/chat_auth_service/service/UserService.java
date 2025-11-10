package com.zxylearn.chat_auth_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxylearn.chat_auth_service.pojo.entity.User;

public interface UserService extends IService<User> {
    boolean addUser(User user);
    User getUserByUsername(String username);
}