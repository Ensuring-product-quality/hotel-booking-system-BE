package com.hotelnow.backend.controller;

import com.hotelnow.backend.dto.ApiResponse;
import com.hotelnow.backend.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/rooms")
public class AvailabilityController {
    private final BookingService bookingService;

    public AvailabilityController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{roomId}/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @PathVariable Long roomId,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut) {
        return ResponseEntity.ok(ApiResponse.success(
                bookingService.checkAvailability(roomId, checkIn, checkOut)));
    }
}
