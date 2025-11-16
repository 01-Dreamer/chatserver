package com.zxylearn.chat_session_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
    @PostMapping("applyAddFriend")
    public boolean applyAddFriend() {
        return true;
    }
}
