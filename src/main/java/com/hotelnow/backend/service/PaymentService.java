package com.hotelnow.backend.service;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.entity.*;
import com.hotelnow.backend.exception.BadRequestException;
import com.hotelnow.backend.exception.ResourceNotFoundException;
import com.hotelnow.backend.repository.BookingRepository;
import com.hotelnow.backend.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public PaymentResponseDTO processPayment(PaymentCreateDTO createDTO) {
        Booking booking = bookingRepository.findById(createDTO.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new BadRequestException("Booking status must be pending_payment to process payment");
        }

        // Validate amount matching (allow a small tolerance for double precision if any, but since BigDecimal is used, direct compare)
        if (createDTO.getAmount().compareTo(booking.getTotalPrice()) != 0) {
            throw new BadRequestException("Payment amount (" + createDTO.getAmount() + 
                    ") does not match booking total price (" + booking.getTotalPrice() + ")");
        }

        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(createDTO.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid payment method. Choose credit_card, paypal, or bank_transfer");
        }

        // Create mock completed payment record
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(createDTO.getAmount())
                .paymentMethod(method)
                .status(PaymentStatus.COMPLETED)
                .transactionalId(UUID.randomUUID().toString())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update booking status
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        return mapToPaymentResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentResponseDTO> searchPayments(Long userId, Long bookingId, String statusStr, Pageable pageable) {
        PaymentStatus status = null;
        if (statusStr != null) {
            try {
                status = PaymentStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }
        Page<Payment> page = paymentRepository.searchPayments(userId, bookingId, status, pageable);
        return PageResponse.fromPage(page.map(this::mapToPaymentResponse));
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
