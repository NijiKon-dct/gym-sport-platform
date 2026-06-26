package com.example.gymbackend.dto.booking;

import java.time.LocalTime;

public record OccupiedSlotResponse(
        LocalTime startTime,
        LocalTime endTime
) {
}
