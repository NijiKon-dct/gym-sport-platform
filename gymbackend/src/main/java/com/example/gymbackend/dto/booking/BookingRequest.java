package com.example.gymbackend.dto.booking;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookingRequest(
        @NotNull Long userId,
        @NotNull Long venueId,
        @NotNull @FutureOrPresent LocalDate date,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        String remark
) {
}

