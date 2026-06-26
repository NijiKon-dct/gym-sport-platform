package com.example.gymbackend.dto.chat;

import jakarta.validation.constraints.NotNull;

public record MarkReadRequest(
        @NotNull Long chatId,
        @NotNull Long userId
) {
}

