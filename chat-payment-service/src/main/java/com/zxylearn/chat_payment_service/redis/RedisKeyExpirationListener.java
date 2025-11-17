package com.zxylearn.chat_payment_service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.zxylearn.chat_payment_service.service.UserService;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
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
        }
    }
}