package com.zxylearn.chat_session_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zxylearn.chat_session_service.Utils.JwtUtil;
import com.zxylearn.chat_session_service.service.UserService;

@RestController
public class SessionController {
    @Autowired
    private UserService userService;

    @PostMapping("applyAddFriend")
    public boolean applyAddFriend(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("friendId") Long friendId) {

        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return false;
        }

        // 判断用户是否存在
        if (!userService.isUserExist(friendId)) {
            return false;
        }

        // 判断用户是否已经是好友关系

        return true;
    }


    // 判断用户是否已经是好友关系
    @PostMapping("acceptAddFriend")
    public boolean acceptAddFriend(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("applicantId") Long applicantId) {
            
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return false;
        }
        return true;

    }
    
}
