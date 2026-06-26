package com.example.gymbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * WebSocket / STOMP 配置，用于实时私聊消息推送
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 客户端订阅前缀
        config.enableSimpleBroker("/queue", "/topic");
        // 客户端发送消息的前缀
        config.setApplicationDestinationPrefixes("/app");
        // 点对点用户前缀 -> /user/{userId}/queue/...
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 端点，前端通过 ws://host/ws-chat 连接（使用 SockJS）
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                      WebSocketHandler wsHandler,
                                                      Map<String, Object> attributes) {
                        if (request instanceof ServletServerHttpRequest servletRequest) {
                            String userId = servletRequest.getServletRequest().getParameter("userId");
                            if (userId != null && !userId.isBlank()) {
                                // 使用 userId 作为 STOMP 用户名，这样 convertAndSendToUser(userId, ...) 才能精确路由
                                return new Principal() {
                                    @Override
                                    public String getName() {
                                        return userId;
                                    }
                                };
                            }
                        }
                        // 未携带 userId 时退回默认逻辑（按 sessionId 区分）
                        return super.determineUser(request, wsHandler, attributes);
                    }
                })
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}




