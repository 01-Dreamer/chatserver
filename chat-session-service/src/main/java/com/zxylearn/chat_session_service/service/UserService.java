package com.zxylearn.chat_session_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zxylearn.chat_session_service.pojo.entity.User;

public interface UserService extends IService<User> {
    public boolean isUserExist(Long userId);
    public User getUser(Long userId);
    public boolean updateUserNickname(Long userId, String newNickname);
    public String updateUserAvatar(Long userId, String newAvatar);
}