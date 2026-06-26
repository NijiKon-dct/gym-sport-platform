package com.example.gymbackend.dto.chat;

import com.example.gymbackend.model.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(
        @NotNull Long chatId,
        @NotNull Long senderId,
        @NotNull Long receiverId,
        @NotBlank String content,
        MessageType type
) {
}

