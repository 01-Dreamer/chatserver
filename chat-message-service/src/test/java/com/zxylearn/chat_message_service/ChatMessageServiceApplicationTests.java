package com.zxylearn.chat_message_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.zxylearn.chat_message_service.redis.RedisService;


@SpringBootTest
class ChatMessageServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private RedisService redisService;


	@Test
	void test() {
		redisService.set("test-key", "test-value");
		String value = redisService.get("test-key");
		System.out.println("Value from Redis: " + value);
	}

}
