package com.example.gymbackend.dto.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Long userId,
        Long venueId,
        String venueName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        Double price,
        boolean paid,
        String remark,
        LocalDateTime createdAt
) {
}

