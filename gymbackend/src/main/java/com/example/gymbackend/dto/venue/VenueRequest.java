package com.example.gymbackend.dto.venue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VenueRequest(
        @NotBlank String name,
        String address,
        String type,
        @NotNull Integer capacity,
        @NotNull Double price,
        String image,
        String description,
        String openTime,
        String closeTime,
        String school
) {
}

