package com.zxylearn.chat_message_service.config;


import com.zxylearn.chat_message_service.websocket.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket // 👈 1. 开启原生的 WebSocket 支持 (注意：不是 @EnableWebSocketMessageBroker)
public class WebSocketConfig implements WebSocketConfigurer {

    // 2. 注入你在上一步创建的 Handler
    @Autowired
    private WebSocketHandler mySimpleWebSocketHandler;

    /**
     * 3. 注册你的 Handler
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        
        // .addHandler()： 告诉 Spring 哪个 Handler 负责处理哪个 URL
        // .setAllowedOrigins("*")： 允许所有来源的跨域连接 (在生产环境中应配置为你的前端域名)
        registry.addHandler(mySimpleWebSocketHandler, "/my-ws-endpoint")
                .setAllowedOrigins("*");
        
        // (可选) 如果你还想支持 SockJS (用于兼容老浏览器)，可以链式调用 .withSockJS()
        // registry.addHandler(mySimpleWebSocketHandler, "/my-ws-endpoint").setAllowedOrigins("*").withSockJS();
    }
}