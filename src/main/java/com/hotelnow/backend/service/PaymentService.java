package com.hotelnow.backend.service;

import com.hotelnow.backend.dto.PageResponse;
import com.hotelnow.backend.dto.PaymentCreateDTO;
import com.hotelnow.backend.dto.PaymentResponseDTO;
import com.hotelnow.backend.entity.*;
import com.hotelnow.backend.exception.BadRequestException;
import com.hotelnow.backend.exception.ResourceNotFoundException;
import com.hotelnow.backend.repository.BookingRepository;
import com.hotelnow.backend.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final CurrentUserService currentUserService;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository,
                          CurrentUserService currentUserService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public PaymentResponseDTO processPayment(PaymentCreateDTO createDTO) {
        Booking booking = bookingRepository.findById(createDTO.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        User current = currentUserService.requireCurrentUser();
        if (!booking.getUser().getId().equals(current.getId())) {
            throw new AccessDeniedException("You cannot pay for another user's booking");
        }
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new BadRequestException("Booking status must be pending_payment to process payment");
        }
        if (paymentRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new BadRequestException("This booking already has a payment");
        }

        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(createDTO.getPaymentMethod().toUpperCase());
        } catch (RuntimeException ex) {
            throw new BadRequestException("Invalid payment method. Choose credit_card, paypal, or bank_transfer");
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalPrice())
                .paymentMethod(method)
                .status(PaymentStatus.COMPLETED)
                .transactionalId(UUID.randomUUID().toString())
                .build();
        Payment savedPayment = paymentRepository.save(payment);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        return mapToPaymentResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentResponseDTO> searchPayments(
            Long requestedUserId, Long bookingId, String statusValue, Pageable pageable) {
        User current = currentUserService.requireCurrentUser();
        Long effectiveUserId = currentUserService.isStaff(current) ? requestedUserId : current.getId();
        PaymentStatus status = parseStatus(statusValue);
        Page<Payment> page = paymentRepository.searchPayments(effectiveUserId, bookingId, status, pageable);
        return PageResponse.fromPage(page.map(this::mapToPaymentResponse));
    }

    private PaymentStatus parseStatus(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return PaymentStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid payment status");
        }
    }

    private PaymentResponseDTO mapToPaymentResponse(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name().toLowerCase())
                .status(payment.getStatus().name().toLowerCase())
                .transactionalId(payment.getTransactionalId())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}
