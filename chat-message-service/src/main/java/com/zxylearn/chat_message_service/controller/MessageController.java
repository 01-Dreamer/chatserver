package com.zxylearn.chat_message_service.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zxylearn.chat_message_service.OSS.OssService;
import com.zxylearn.chat_message_service.Utils.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class MessageController {

    @Autowired
    private OssService ossService;

    @PostMapping("/uploadFile")
    public String uploadFile(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestParam("file") MultipartFile file) {

        String token = authorizationHeader.replace("Bearer ", "");
        if(!JwtUtil.verifyToken(token)) {
            return null;
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return null;
        }
        return ossService.uploadFile(file);
    }
}