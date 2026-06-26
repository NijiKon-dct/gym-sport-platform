package com.example.gymbackend.controller;

import com.example.gymbackend.dto.ApiResponse;
import com.example.gymbackend.dto.booking.BookingRequest;
import com.example.gymbackend.dto.booking.BookingResponse;
import com.example.gymbackend.dto.booking.OccupiedSlotResponse;
import com.example.gymbackend.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public ApiResponse<List<BookingResponse>> listBookings(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ApiResponse.ok(bookingService.listByUser(userId));
        }
        return ApiResponse.ok(bookingService.listAll());
    }

    @GetMapping("/range")
    public ApiResponse<List<BookingResponse>> listByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ApiResponse.ok(bookingService.listByDateRange(start, end));
    }

    /**
     * 查询某日某场馆已被预约的时段（不含已取消），供预约页展示与前端重叠校验。
     */
    @GetMapping("/occupied-slots")
    public ApiResponse<List<OccupiedSlotResponse>> listOccupiedSlots(
            @RequestParam Long venueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ApiResponse.ok(bookingService.listOccupiedSlots(venueId, date));
    }

    @PostMapping
    public ApiResponse<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return ApiResponse.ok(bookingService.create(request));
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<BookingResponse> cancel(@PathVariable Long id) {
        return ApiResponse.ok(bookingService.cancel(id));
    }

    @PatchMapping("/{id}/confirm")
    public ApiResponse<BookingResponse> confirm(@PathVariable Long id) {
        return ApiResponse.ok(bookingService.confirm(id));
    }

    @PatchMapping("/{id}/pay")
    public ApiResponse<BookingResponse> pay(@PathVariable Long id) {
        return ApiResponse.ok(bookingService.pay(id));
    }

    /**
     * 查询全平台预约的平均确认用时（秒）
     */
    @GetMapping("/stats/average-confirm-time")
    public ApiResponse<Long> getAverageConfirmTime() {
        return ApiResponse.ok(bookingService.getAverageConfirmSeconds());
    }
}

