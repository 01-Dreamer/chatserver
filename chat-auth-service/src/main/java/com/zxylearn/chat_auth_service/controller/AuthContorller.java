package com.zxylearn.chat_auth_service.controller;

import org.springframework.web.bind.annotation.RestController;

import com.zxylearn.chat_auth_service.Utils.JwtUtil;
import com.zxylearn.chat_auth_service.Utils.PasswordUtil;
import com.zxylearn.chat_auth_service.pojo.DTO.UserLoginRequestDTO;
import com.zxylearn.chat_auth_service.pojo.DTO.UserLoginResponseDTO;
import com.zxylearn.chat_auth_service.pojo.DTO.UserRegisterRequestDTO;
import com.zxylearn.chat_auth_service.pojo.DTO.UserRegisterResponseDTO;
import com.zxylearn.chat_auth_service.pojo.entity.User;
import com.zxylearn.chat_auth_service.redis.RedisService;
import com.zxylearn.chat_auth_service.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class AuthContorller {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @PostMapping("/login")
    public UserLoginResponseDTO login(@Valid @RequestBody UserLoginRequestDTO request) {
        UserLoginResponseDTO response = new UserLoginResponseDTO();
        response.setUserId(-1L);
        User user = userService.getUserByUsername(request.getUsername());
        if(user != null && PasswordUtil.match(request.getPassword(), user.getPasswordHash())) {
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setNickname(user.getNickname());
            response.setAvatar(user.getAvatar());
            response.setToken(JwtUtil.createToken(user.getId().toString()));
        }
        return response;
    }

    @PostMapping("/register")
    public UserRegisterResponseDTO register(@Valid @RequestBody UserRegisterRequestDTO request) {
        UserRegisterResponseDTO response = new UserRegisterResponseDTO(-1L);
        if(userService.getUserByUsername(request.getUsername()) == null) {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPasswordHash(PasswordUtil.encode(request.getPassword()));
            user.setNickname(request.getNickname());
            userService.addUser(user);
            response.setUserId(user.getId());
        }
        return response;
    }

    @PostMapping("setUserInfo")
    public boolean setUserInfo(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestParam("uuid") String uuid) {
        
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return false;
        }
        return redisService.addUserInfoForApp(uuid, userId.toString());
    }

    @GetMapping("getUserInfo")
    public String getUserInfo(@RequestParam("uuid") String uuid) {
        return redisService.getandDeleteUserInfoForApp(uuid);
    }
}
