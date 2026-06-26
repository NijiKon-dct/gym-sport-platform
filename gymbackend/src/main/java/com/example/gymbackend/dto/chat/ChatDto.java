package com.example.gymbackend.dto.chat;

import java.time.LocalDateTime;

public record ChatDto(
        Long id,
        Long userId,
        String username,
        String avatar,
        String lastMessage,
        LocalDateTime lastMessageTime,
        Integer unreadCount,
        Boolean isOnline
) {
}

