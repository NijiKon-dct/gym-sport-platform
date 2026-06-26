package com.example.gymbackend.dto.social;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        Long userId,
        String username,
        String avatar,
        String content,
        List<String> images,
        String video,
        Long venueId,
        String venueName,
        List<String> topics,
        List<Long> mentionedUserIds,
        Integer likes,
        Integer comments,
        Boolean isLiked,
        Boolean isPinned,
        Boolean isFeatured,
        Boolean isHot,
        Integer hotScore,
        LocalDateTime createdAt
) {
}

