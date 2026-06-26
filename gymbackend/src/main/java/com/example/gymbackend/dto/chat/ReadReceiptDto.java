package com.example.gymbackend.dto.chat;

import java.time.LocalDateTime;

/**
 * WebSocket 已读回执 DTO
 */
public record ReadReceiptDto(
        Long chatId,
        Long readerId,
        LocalDateTime readAt
) {
}






