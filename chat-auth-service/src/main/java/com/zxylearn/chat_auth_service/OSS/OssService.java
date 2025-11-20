package com.zxylearn.chat_auth_service.OSS;

import org.springframework.stereotype.Service;

import com.aliyun.oss.OSS;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


@Slf4j
@Service
public class OssService {

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    public boolean createAvatar(Long userId) {
        String sourceKey = "chat/UserAvatar.png";
        String destinationKey = "chat/UserAvatar_" + userId + ".png";
        try {
            ossClient.copyObject(bucketName, sourceKey, bucketName, destinationKey);
            return true;
        } catch (Exception e) {
            log.error("failed to copy default avatar: {}", e.getMessage());
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