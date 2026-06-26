package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import com.example.gymbackend.dto.venue.VenueRequest;
import com.example.gymbackend.dto.venue.VenueResponse;
import com.example.gymbackend.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    @GetMapping
    public ApiResponse<List<VenueResponse>> listVenues(@RequestParam(required = false) Long userId) {
        return ApiResponse.ok(venueService.findAll(userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<VenueResponse> getVenue(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        return ApiResponse.ok(venueService.findById(id, userId));
    }

    @PostMapping
    public ApiResponse<VenueResponse> createVenue(@Valid @RequestBody VenueRequest request) {
        return ApiResponse.ok(venueService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<VenueResponse> updateVenue(@PathVariable Long id, @Valid @RequestBody VenueRequest request) {
        return ApiResponse.ok(venueService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteVenue(@PathVariable Long id) {
        venueService.delete(id);
        return ApiResponse.ok(null);
    }
}

