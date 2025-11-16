package com.zxylearn.chat_message_service.OSS;

import org.springframework.stereotype.Service;


import com.aliyun.oss.OSS;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;


@Slf4j
@Service
public class OssService {

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    public String uploadFile(MultipartFile file) {
        try {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String objectName = "chat/" + uuid + "_" + file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();
            ossClient.putObject(bucketName, objectName, inputStream);
            return "https://" + bucketName + "." + endpoint + "/" + objectName;
        } catch (Exception e) {
            log.error("failed to upload file: {}", e.getMessage());
            return null;
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