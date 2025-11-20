package com.zxylearn.chat_payment_service.redis;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxylearn.chat_payment_service.hutool.IdGeneratorService;
import com.zxylearn.chat_payment_service.service.MessageService;
import com.zxylearn.chat_payment_service.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

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

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message messageFromRedis, byte[] pattern) {
        String expiredKey = messageFromRedis.toString();
        if (expiredKey != null && expiredKey.startsWith("red_packet:")) {
            int seqPos = expiredKey.lastIndexOf(":");
            String redPacketId = expiredKey.substring(seqPos + 1);
            String key = "red_packet_owner:" + redPacketId;
            String redPacket = redisService.get(key);
            redisService.del(key);

            seqPos = redPacket.lastIndexOf(":");
            String userId = redPacket.substring(0, seqPos);
            String amount = redPacket.substring(seqPos + 1);
            userService.increaseBalance(Long.parseLong(userId), Long.parseLong(amount));

            // 发送红包退款通知
            try {
                Map<String, String> content = new HashMap();
                content.put("payerId", userId.toString());
                content.put("receiverId", userId.toString());
                content.put("amount", amount.toString());
                String processedContent = objectMapper.writeValueAsString(content);

                com.zxylearn.chat_payment_service.pojo.entity.Message message = new com.zxylearn.chat_payment_service.pojo.entity.Message();
                message.setId(idGeneratorService.getId());
                message.setSessionId(-1L);
                message.setSenderId(-1L);
                message.setType("red_packet_info");
                message.setCreateTime(new Date());
                message.setContent(processedContent);
                messageService.save(message);
                String processedMessage = objectMapper.writeValueAsString(message);
                if (redisService.isUserOnline(userId.toString())) {
                    redisService.publish(userId.toString(), processedMessage);
                } else {
                    redisService.addUnreadMessage(userId.toString(), message.getId().toString());
                }
            } catch (Exception e) {
                log.error("failed to send red packet refund notification for userId: " + userId, e.getMessage());
            }
        }
    }
}