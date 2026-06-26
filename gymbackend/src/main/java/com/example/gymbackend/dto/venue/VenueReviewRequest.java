package com.example.gymbackend.dto.venue;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record VenueReviewRequest(
        @NotNull(message = "评分不能为空")
        @Min(value = 1, message = "评分不能小于1")
        @Max(value = 5, message = "评分不能大于5")
        Integer rating,
        
        @Size(max = 1000, message = "评论内容不能超过1000字")
        String content,
        
        List<String> imagePaths,
        
        List<String> videoPaths
) {
}















