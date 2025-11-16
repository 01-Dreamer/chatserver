package com.zxylearn.chat_message_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.zxylearn.chat_message_service.OSS.OssService;


@SpringBootTest
class ChatMessageServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private OssService ossService;


	@Test
	void test() {
		String fileContent = "Test";
        byte[] contentBytes = fileContent.getBytes();

        MultipartFile testFile = new MockMultipartFile(
            "file",
            "hello.txt",
            "text/plain",
            contentBytes
        );
        ossService.uploadFile(testFile);
	}

}
