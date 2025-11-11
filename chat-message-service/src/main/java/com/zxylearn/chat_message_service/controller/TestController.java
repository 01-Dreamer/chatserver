package com.zxylearn.chat_message_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.zxylearn.chat_message_service.hutool.IdGeneratorService;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class TestController {
    @Autowired
	private IdGeneratorService idGeneratorService;

    @GetMapping("/test")
    public long test() {
        return idGeneratorService.getId();
    }
    


}