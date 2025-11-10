package com.zxylearn.chat_message_service.controller;


import com.zxylearn.chat_message_service.redis.RedisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class TestController {
    @Autowired
	private RedisService redisService;


    @GetMapping("/subscribe/{channelName}")
    public boolean subscribe(@PathVariable String channelName) {
        return redisService.subscribe(channelName);
    }

    @GetMapping("/unsubscribe/{channelName}")
    public boolean unsubscribe(@PathVariable String channelName) {
        return redisService.unsubscribe(channelName);
    }

    @GetMapping("/get/{key}")
    public String getKey(@PathVariable String key) {
        return redisService.get(key);
    }
    


}