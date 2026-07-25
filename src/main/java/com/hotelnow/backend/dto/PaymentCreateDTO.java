package com.hotelnow.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCreateDTO {
    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    /**
     * Kept temporarily for old clients. The server always charges the trusted
     * booking total and never trusts an amount supplied by the browser.
     */
    @Deprecated
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}
