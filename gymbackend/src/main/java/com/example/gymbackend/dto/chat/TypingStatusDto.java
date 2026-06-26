package com.example.gymbackend.dto.chat;

/**
 * WebSocket 输入中状态 DTO
 */
public record TypingStatusDto(
        Long chatId,
        Long fromUserId,
        Long toUserId,
        boolean typing
) {
}






