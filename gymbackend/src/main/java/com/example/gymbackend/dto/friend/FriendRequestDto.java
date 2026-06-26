package com.example.gymbackend.dto.friend;

import java.time.LocalDateTime;

public record FriendRequestDto(
        Long id,
        Long fromUserId,
        String fromUsername,
        String fromAvatar,
        String message,
        String status,
        LocalDateTime createdAt
) {
}

