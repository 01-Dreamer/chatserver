package com.zxylearn.chat_message_service.websocket;


import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 简单的 WebSocket 处理器
 * 1. 必须是 Spring Bean (@Component)
 * 2. 继承 TextWebSocketHandler 来处理纯文本消息
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    /**
     * 当 WebSocket 连接成功建立后，调用此方法
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // session.getId() 是 Spring 自动分配的唯一 ID
        System.out.println("====================================");
        System.out.println("【新连接】: " + session.getId());
        System.out.println("【连接 IP】: " + session.getRemoteAddress());
        System.out.println("====================================");
    }

    /**
     * 当收到一个文本消息时，调用此方法
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // message.getPayload() 就是客户端发来的原始字符串
        String payload = message.getPayload();
        
        System.out.println("====================================");
        System.out.println("【收到消息】: " + payload);
        System.out.println("【来自】: " + session.getId());
        System.out.println("====================================");

        // (可选) 自动回复一条消息给客户端
        session.sendMessage(new TextMessage("服务器已收到你的消息: " + payload));
    }

    /**
     * 当 WebSocket 连接关闭后，调用此方法
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("====================================");
        System.out.println("【连接关闭】: " + session.getId());
        System.out.println("【关闭状态】: " + status);
        System.out.println("====================================");
    }

    /**
     * 当传输发生错误时，调用此方法
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("【连接出错】: " + session.getId() + "，异常: " + exception.getMessage());
    }
}