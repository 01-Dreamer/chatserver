package com.zxylearn.chat_auth_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.zxylearn.chat_auth_service.OSS.OssService;

@SpringBootTest
class ChatAuthServiceApplicationTests {

	@Autowired
	private OssService ossService;

	@Test
	void contextLoads() {
		ossService.createAvatar(1L);
		ossService.createAvatar(2L);
	}

}
