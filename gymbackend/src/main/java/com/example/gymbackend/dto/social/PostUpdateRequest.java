package com.example.gymbackend.dto.social;

import java.util.List;

public record PostUpdateRequest(
        String content,
        List<String> images,
        String video,
        Long venueId,
        List<String> topics,
        List<Long> mentionedUserIds
) {
}

