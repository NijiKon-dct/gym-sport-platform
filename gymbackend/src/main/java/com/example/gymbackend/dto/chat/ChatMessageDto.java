package com.example.gymbackend.dto.chat;

import java.time.LocalDateTime;

public record ChatMessageDto(
        Long id,
        Long chatId,
        Long senderId,
        Long receiverId,
        String content,
        String type,
        Boolean isRead,
        LocalDateTime createdAt
) {
}

