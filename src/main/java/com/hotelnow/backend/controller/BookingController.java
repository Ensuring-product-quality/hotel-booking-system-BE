package com.hotelnow.backend.controller;

import java.util.Set;
import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.service.BookingService;
import com.hotelnow.backend.util.PageableFactory;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @Valid @RequestBody BookingCreateDTO createDTO) {
        BookingResponseDTO data = bookingService.createBooking(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Booking placed. Please complete payment within 15 minutes.",
                        data,
                        HttpStatus.CREATED.value()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getBookings(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {
        Pageable pageable = PageableFactory.create(page, size, sort, Set.of("id", "checkInDate", "checkOutDate", "guests", "totalPrice", "status", "createdAt", "updatedAt"));
        return ResponseEntity.ok(ApiResponse.success(
                bookingService.searchBookings(userId, status, keyword, pageable)));
    }

    @GetMapping("/public/lookup")
    public ResponseEntity<ApiResponse<BookingDetailDTO>> publicLookup(
            @RequestParam Long bookingId,
            @RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.publicLookup(bookingId, email)));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingDetailDTO>> getBookingById(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingDetail(bookingId)));
    }

    @GetMapping("/{bookingId}/payment-status")
    public ResponseEntity<ApiResponse<String>> getPaymentStatus(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Payment status",
                bookingService.getPaymentStatus(bookingId),
                HttpStatus.OK.value()));
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingUpdateDTO updateDTO) {
        return ResponseEntity.ok(ApiResponse.success(
                "Booking updated successfully",
                bookingService.updateBooking(bookingId, updateDTO),
                HttpStatus.OK.value()));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}
