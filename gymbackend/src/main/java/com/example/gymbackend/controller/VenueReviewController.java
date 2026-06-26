package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import com.example.gymbackend.dto.venue.VenueReviewRequest;
import com.example.gymbackend.dto.venue.VenueReviewResponse;
import com.example.gymbackend.service.VenueReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/venues/{venueId}/reviews")
public class VenueReviewController {

    private final VenueReviewService reviewService;

    @GetMapping
    public ApiResponse<List<VenueReviewResponse>> getReviews(@PathVariable Long venueId) {
        return ApiResponse.ok(reviewService.findByVenueId(venueId));
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getReviewStats(@PathVariable Long venueId) {
        Double avgRating = reviewService.getAverageRating(venueId);
        Long reviewCount = reviewService.getReviewCount(venueId);
        return ApiResponse.ok(Map.of(
                "averageRating", avgRating,
                "reviewCount", reviewCount
        ));
    }

    @PostMapping
    public ApiResponse<VenueReviewResponse> createReview(
            @PathVariable Long venueId,
            @RequestParam Long userId,
            @Valid @RequestBody VenueReviewRequest request) {
        return ApiResponse.ok(reviewService.create(venueId, userId, request));
    }

    @PutMapping("/{reviewId}")
    public ApiResponse<VenueReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId,
            @Valid @RequestBody VenueReviewRequest request) {
        return ApiResponse.ok(reviewService.update(reviewId, userId, request));
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        reviewService.delete(reviewId, userId);
        return ApiResponse.ok(null);
    }
}















