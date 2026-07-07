package com.hotelnow.backend.controller;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(@Valid @RequestBody BookingCreateDTO createDTO) {
        BookingResponseDTO data = bookingService.createBooking(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking placed. Please complete payment within 15 minutes.", data, HttpStatus.CREATED.value()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getBookings(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {

        String[] sortParams = sort.split(",");
        Sort sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        PageResponse<BookingResponseDTO> data = bookingService.searchBookings(userId, status, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingDetailDTO>> getBookingById(@PathVariable Long bookingId) {
        BookingDetailDTO data = bookingService.getBookingDetail(bookingId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{bookingId}/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @PathVariable Long bookingId,
            @RequestParam Long roomId,
            @RequestParam String checkIn,
            @RequestParam String checkOut) {
        
        LocalDate checkInDate = LocalDate.parse(checkIn);
        LocalDate checkOutDate = LocalDate.parse(checkOut);
        boolean available = bookingService.checkAvailability(roomId, checkInDate, checkOutDate);
        return ResponseEntity.ok(ApiResponse.success("Availability status", available, HttpStatus.OK.value()));
    }

    @GetMapping("/{bookingId}/payment-status")
    public ResponseEntity<ApiResponse<String>> getPaymentStatus(@PathVariable Long bookingId) {
        String paymentStatus = bookingService.getPaymentStatus(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Payment status", paymentStatus, HttpStatus.OK.value()));
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingUpdateDTO updateDTO) {
        BookingResponseDTO data = bookingService.updateBooking(bookingId, updateDTO);
        return ResponseEntity.ok(ApiResponse.success("Booking updated successfully", data, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Booking cancelled successfully", null, HttpStatus.NO_CONTENT.value()));
    }
}
