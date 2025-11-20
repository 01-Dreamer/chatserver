package com.zxylearn.chat_session_service.OSS;

import org.springframework.stereotype.Service;


import com.aliyun.oss.OSS;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


@Slf4j
@Service
public class OssService {

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    public boolean uploadFile(MultipartFile file, Long userId) {
        try {
            String objectName = "chat/UserAvatar_" + userId.toString() + ".png";
            InputStream inputStream = file.getInputStream();
            ossClient.putObject(bucketName, objectName, inputStream);
            return true;
        } catch (Exception e) {
            log.error("failed to upload file: {}", e.getMessage());
            return false;
        }
    }

    public boolean deleteFile(String objectName) {
        try {
            ossClient.deleteObject(bucketName, objectName);
            return true;
        } catch (Exception e) {
            log.error("failed to delete file: {}", e.getMessage());
            return false;
        }
    }
}