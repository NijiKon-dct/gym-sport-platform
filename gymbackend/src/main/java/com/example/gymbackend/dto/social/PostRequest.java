package com.example.gymbackend.dto.social;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PostRequest(
        @NotNull Long userId,
        @NotBlank String content,
        List<String> images,
        String video,
        Long venueId,
        List<String> topics,
        List<Long> mentionedUserIds
) {
}

