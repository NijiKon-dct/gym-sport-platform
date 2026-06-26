package com.example.gymbackend.controller;

import com.example.gymbackend.dto.chat.TypingStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * 基于 STOMP 的聊天 WebSocket 控制器
 * 目前只处理「正在输入」等无需持久化的实时事件
 */
@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 输入中状态上报
     * 前端发送到：/app/chat/typing
     * 由后端转发到：/topic/user.{toUserId}.typing
     */
    @MessageMapping("/chat/typing")
    public void typing(TypingStatusDto typingStatus) {
        if (typingStatus == null || typingStatus.toUserId() == null) {
            return;
        }
        Long toUserId = typingStatus.toUserId();
        messagingTemplate.convertAndSend(
                "/topic/user." + toUserId + ".typing",
                typingStatus
        );
    }
}






