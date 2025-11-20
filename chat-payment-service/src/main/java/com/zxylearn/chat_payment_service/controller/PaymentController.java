package com.zxylearn.chat_payment_service.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxylearn.chat_payment_service.Utils.JwtUtil;
import com.zxylearn.chat_payment_service.hutool.IdGeneratorService;
import com.zxylearn.chat_payment_service.pojo.entity.Message;
import com.zxylearn.chat_payment_service.redis.RedisService;
import com.zxylearn.chat_payment_service.service.MessageService;
import com.zxylearn.chat_payment_service.service.UserService;


@RestController
public class PaymentController {
    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    @Autowired
    private IdGeneratorService idGeneratorService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("payToUser")
    public boolean payToUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("userId") Long userId,
            @RequestParam("amount") Long amount) throws Exception {
 
        String token = authorizationHeader.replace("Bearer ", "");
        Long payerId = JwtUtil.getUserId(token);
        if (payerId == null) {
            return false;
        }

        // 判断用户是否存在
        if(!userService.isUserExists(userId)) {
            return false;
        }

        // 扣款
        if(!userService.descreaseBalance(payerId, amount)) {
            return false;
        }

        // 收款
        userService.increaseBalance(userId, amount);

        // 发送红包通知
        Map<String, String> content = new HashMap();
        content.put("payerId", payerId.toString());
        content.put("receiverId", userId.toString());
        content.put("amount", amount.toString());
        String processedContent = objectMapper.writeValueAsString(content);

        Message message = new Message();
        message.setId(idGeneratorService.getId());
        message.setSessionId(-1L);
        message.setSenderId(-1L);
        message.setType("red_packet_info");
        message.setCreateTime(new Date());
        message.setContent(processedContent);
        messageService.save(message);
        String processedMessage = objectMapper.writeValueAsString(message);
        if (redisService.isUserOnline(payerId.toString())) {
            redisService.publish(payerId.toString(), processedMessage);
        } else {
            redisService.addUnreadMessage(payerId.toString(), message.getId().toString());
        }
        if (redisService.isUserOnline(userId.toString())) {
            redisService.publish(userId.toString(), processedMessage);
        } else {
            redisService.addUnreadMessage(userId.toString(), message.getId().toString());
        }

        return true;
    }

    @PostMapping("setRedPacket")
    public boolean setRedPacket(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("redPacketId") String redPacketId,
            @RequestParam("amount") Long amount) throws Exception {
        
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return false;
        }

        String key = "red_packet:" + redPacketId;
        if(redisService.exist(key)) {
            return false;
        }

        // 扣款
        if(!userService.descreaseBalance(userId, amount)) {
            return false;
        }

        // 缓存红包信息
        if(!redisService.setRedPacket(redPacketId, userId.toString(), amount.toString())) {
            userService.increaseBalance(userId, amount);
            return false;
        }

        return true;
    }

    @GetMapping("getRedPacket")
    public boolean getRedPacket(
        @RequestParam("userId") Long userId,
        @RequestParam("redPacketId") String redPacketId) throws Exception {

        String key = "red_packet:" + redPacketId;
        if(!redisService.exist(key)) {
            return false;
        }

        // 判断用户是否存在
        if(!userService.isUserExists(userId)) {
            return false;
        }

        // 领取红包
        String redPacket = redisService.getAndDeleteRedPacket(redPacketId);
        if(redPacket == null) {
            return false;
        }
        int seqPos = redPacket.lastIndexOf(":");
        String payerId = redPacket.substring(0, seqPos);
        String amount = redPacket.substring(seqPos + 1);
        userService.increaseBalance(userId, Long.parseLong(amount));

        // 发送红包通知
        Map<String, String> content = new HashMap();
        content.put("payerId", payerId.toString());
        content.put("receiverId", userId.toString());
        content.put("amount", amount.toString());
        String processedContent = objectMapper.writeValueAsString(content);

        Message message = new Message();
        message.setId(idGeneratorService.getId());
        message.setSessionId(-1L);
        message.setSenderId(-1L);
        message.setType("red_packet_info");
        message.setCreateTime(new Date());
        message.setContent(processedContent);
        messageService.save(message);
        String processedMessage = objectMapper.writeValueAsString(message);
        if (redisService.isUserOnline(payerId.toString())) {
            redisService.publish(payerId.toString(), processedMessage);
        } else {
            redisService.addUnreadMessage(payerId.toString(), message.getId().toString());
        }
        if (redisService.isUserOnline(userId.toString())) {
            redisService.publish(userId.toString(), processedMessage);
        } else {
            redisService.addUnreadMessage(userId.toString(), message.getId().toString());
        }

        return true;
    }

    @GetMapping("getBalance")
    public Long getBalance(
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
 
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return null;
        }

        return userService.getBalance(userId);
    }
}
