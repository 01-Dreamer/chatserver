package com.zxylearn.chat_message_service.config;


import com.zxylearn.chat_message_service.redis.RedisQueueListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ApplicationStartRunnerConfig implements ApplicationRunner {

    @Autowired
    private RedisQueueListener redisQueueListener;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        redisQueueListener.msgProcessListening();
    }
}