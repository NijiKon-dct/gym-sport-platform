package com.example.gymbackend.dto.topic;

import java.time.LocalDateTime;

public record TopicResponse(
        Long id,
        String name,
        String description,
        Integer postCount,
        Integer subscriberCount,
        Boolean isSubscribed,
        LocalDateTime createdAt
) {
}
















