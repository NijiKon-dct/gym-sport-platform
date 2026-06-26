package com.example.gymbackend.dto.social;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long id,
        Long postId,
        Long userId,
        String username,
        String avatar,
        String content,
        Integer likes,
        Boolean isLiked,
        Long parentCommentId,
        String parentCommentUsername,
        List<CommentResponse> replies,
        LocalDateTime createdAt
) {
}

