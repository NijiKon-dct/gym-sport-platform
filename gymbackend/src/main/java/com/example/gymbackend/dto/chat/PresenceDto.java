package com.example.gymbackend.dto.chat;

/**
 * WebSocket 在线状态 DTO
 */
public record PresenceDto(
        Long userId,
        boolean online
) {
}






