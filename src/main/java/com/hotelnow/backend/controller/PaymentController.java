package com.hotelnow.backend.controller;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> processPayment(@Valid @RequestBody PaymentCreateDTO createDTO) {
        PaymentResponseDTO data = paymentService.processPayment(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment processed successfully", data, HttpStatus.CREATED.value()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponseDTO>>> getPayments(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {

        String[] sortParams = sort.split(",");
        Sort sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        PageResponse<PaymentResponseDTO> data = paymentService.searchPayments(userId, bookingId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
