package com.zxylearn.chat_message_service.websocket;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.zxylearn.chat_message_service.redis.RedisService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private RedisService redisService;

    private static final Map<String, WebSocketSession> UserId2Sesssion = new ConcurrentHashMap<>();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        UserId2Sesssion.put(userId, session);
        redisService.addOnlineUser(userId, LocalDateTime.now().format(formatter));
        log.info("User {} connected", userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload();
        redisService.rpush("msg-process-queue", msg);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        redisService.removeOnlineUser(userId);
        UserId2Sesssion.remove(userId);
        log.info("User {} disconnected", userId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        redisService.removeOnlineUser(userId);
        UserId2Sesssion.remove(userId);
        log.info("User {} disconnected(error)", userId);
    }

    public boolean sendMessageToUser(String userId, String message) throws Exception {
        WebSocketSession session = UserId2Sesssion.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
            return true;
        }
        return false;
    }
}