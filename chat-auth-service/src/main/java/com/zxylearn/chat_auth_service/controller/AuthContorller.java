package com.zxylearn.chat_auth_service.controller;

import org.springframework.web.bind.annotation.RestController;

import com.zxylearn.chat_auth_service.Utils.JwtUtil;
import com.zxylearn.chat_auth_service.Utils.PasswordUtil;
import com.zxylearn.chat_auth_service.pojo.DTO.UserLoginRequestDTO;
import com.zxylearn.chat_auth_service.pojo.DTO.UserLoginResponseDTO;
import com.zxylearn.chat_auth_service.pojo.DTO.UserRegisterRequestDTO;
import com.zxylearn.chat_auth_service.pojo.DTO.UserRegisterResponseDTO;
import com.zxylearn.chat_auth_service.pojo.entity.User;
import com.zxylearn.chat_auth_service.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class AuthContorller {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public UserLoginResponseDTO login(@Valid @RequestBody UserLoginRequestDTO request) {
        UserLoginResponseDTO response = new UserLoginResponseDTO(-1L, null);
        User user = userService.getUserByUsername(request.getUsername());
        if(user != null && PasswordUtil.match(request.getPassword(), user.getPasswordHash())) {
            response.setUserId(user.getId());
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
            user.setNickname(request.getUsername());
            userService.addUser(user);
            response.setUserId(user.getId());
        }
        return response;
    }

}
