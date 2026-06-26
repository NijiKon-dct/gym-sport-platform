package com.example.gymbackend.dto.friend;

import java.time.LocalDateTime;

public record FriendDto(
        Long id,
        String username,
        String nickname,
        String avatar,
        String school,
        Boolean isOnline,
        LocalDateTime lastSeen
) {
}

