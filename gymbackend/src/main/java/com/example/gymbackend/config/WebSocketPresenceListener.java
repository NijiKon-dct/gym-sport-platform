package com.example.gymbackend.config;

import com.example.gymbackend.dto.chat.PresenceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听 STOMP 连接/断开事件，实现在线状态推送
 */
@Component
@RequiredArgsConstructor
public class WebSocketPresenceListener {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 记录每个用户的连接数（支持多端、多标签）
     */
    private final Map<Long, Integer> onlineCount = new ConcurrentHashMap<>();

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        if (user == null) {
            return;
        }
        Long userId;
        try {
            userId = Long.valueOf(user.getName());
        } catch (NumberFormatException e) {
            return;
        }
        onlineCount.merge(userId, 1, Integer::sum);

        // 第一次连接时广播 online=true
        if (onlineCount.getOrDefault(userId, 0) == 1) {
            messagingTemplate.convertAndSend(
                    "/topic/presence",
                    new PresenceDto(userId, true)
            );
        }
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        if (user == null) {
            return;
        }
        Long userId;
        try {
            userId = Long.valueOf(user.getName());
        } catch (NumberFormatException e) {
            return;
        }
        onlineCount.computeIfPresent(userId, (id, count) -> Math.max(0, count - 1));
        int remain = onlineCount.getOrDefault(userId, 0);
        if (remain <= 0) {
            onlineCount.remove(userId);
            // 最后一个连接断开时广播 online=false
            messagingTemplate.convertAndSend(
                    "/topic/presence",
                    new PresenceDto(userId, false)
            );
        }
    }
}






