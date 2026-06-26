package com.example.gymbackend.dto.venue;

import java.time.LocalDateTime;
import java.util.List;

public record VenueReviewResponse(
        Long id,
        Long venueId,
        Long userId,
        String username,
        String userAvatar,
        Integer rating,
        String content,
        List<String> imagePaths,
        List<String> videoPaths,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}















