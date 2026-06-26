package com.example.gymbackend.dto.venue;

public record VenueResponse(
        Long id,
        String name,
        String address,
        String type,
        Integer capacity,
        Double price,
        String image,
        String description,
        String openTime,
        String closeTime,
        String school,
        boolean isInUse,
        Boolean isFavorite,
        Double averageRating,
        Long reviewCount
) {
}

