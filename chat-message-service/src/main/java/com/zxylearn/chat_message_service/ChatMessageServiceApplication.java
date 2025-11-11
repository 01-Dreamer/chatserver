package com.zxylearn.chat_message_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ChatMessageServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ChatMessageServiceApplication.class, args);
	}
}
