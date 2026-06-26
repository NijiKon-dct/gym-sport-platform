package com.example.gymbackend.dto.social;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequest(
        @NotNull Long userId,
        @NotBlank String content,
        Long parentCommentId // 父评论ID（如果是回复）
) {
}

