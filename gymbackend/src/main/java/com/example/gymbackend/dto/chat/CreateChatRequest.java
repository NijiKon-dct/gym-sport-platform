package com.example.gymbackend.dto.chat;

import jakarta.validation.constraints.NotNull;

public record CreateChatRequest(
        @NotNull Long currentUserId,
        @NotNull Long targetUserId
) {
}

