package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import com.example.gymbackend.dto.venue.VenueResponse;
import com.example.gymbackend.model.VenueFavorite;
import com.example.gymbackend.service.VenueInteractionService;
import com.example.gymbackend.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/venues")
public class VenueInteractionController {

    private final VenueInteractionService interactionService;
    private final VenueService venueService;

    // ========== 收藏相关 ==========
    
    @PostMapping("/{venueId}/favorite")
    public ApiResponse<Void> addFavorite(@PathVariable Long venueId, @RequestParam Long userId) {
        interactionService.addFavorite(userId, venueId);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{venueId}/favorite")
    public ApiResponse<Void> removeFavorite(@PathVariable Long venueId, @RequestParam Long userId) {
        interactionService.removeFavorite(userId, venueId);
        return ApiResponse.ok(null);
    }

    @GetMapping("/{venueId}/favorite")
    public ApiResponse<Boolean> isFavorite(@PathVariable Long venueId, @RequestParam Long userId) {
        return ApiResponse.ok(interactionService.isFavorite(userId, venueId));
    }

    @GetMapping("/favorites")
    public ApiResponse<List<VenueFavorite>> getUserFavorites(@RequestParam Long userId) {
        return ApiResponse.ok(interactionService.getUserFavorites(userId));
    }

    @GetMapping("/favorites/list")
    public ApiResponse<List<VenueResponse>> getFavoriteVenues(@RequestParam Long userId) {
        return ApiResponse.ok(venueService.findFavoriteVenues(userId));
    }
}

