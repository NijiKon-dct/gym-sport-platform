package com.example.gymbackend.dto.friend;

import jakarta.validation.constraints.NotNull;

public record FriendRequestCreateRequest(
        @NotNull Long fromUserId,
        @NotNull Long toUserId,
        String message
) {
}

