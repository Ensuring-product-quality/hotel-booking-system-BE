package com.hotelnow.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponseDTO {
    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String transactionalId;
    private LocalDateTime paymentDate;

    public PaymentResponseDTO() {}

    public PaymentResponseDTO(Long id, Long bookingId, BigDecimal amount, String paymentMethod, String status, String transactionalId, LocalDateTime paymentDate) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionalId = transactionalId;
        this.paymentDate = paymentDate;
    }

    public static PaymentResponseDTOBuilder builder() {
        return new PaymentResponseDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionalId() { return transactionalId; }
    public void setTransactionalId(String transactionalId) { this.transactionalId = transactionalId; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public static class PaymentResponseDTOBuilder {
        private Long id;
        private Long bookingId;
        private BigDecimal amount;
        private String paymentMethod;
        private String status;
        private String transactionalId;
        private LocalDateTime paymentDate;

        public PaymentResponseDTOBuilder id(Long id) { this.id = id; return this; }
        public PaymentResponseDTOBuilder bookingId(Long bookingId) { this.bookingId = bookingId; return this; }
        public PaymentResponseDTOBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public PaymentResponseDTOBuilder paymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public PaymentResponseDTOBuilder status(String status) { this.status = status; return this; }
        public PaymentResponseDTOBuilder transactionalId(String transactionalId) { this.transactionalId = transactionalId; return this; }
        public PaymentResponseDTOBuilder paymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; return this; }

        public PaymentResponseDTO build() {
            return new PaymentResponseDTO(id, bookingId, amount, paymentMethod, status, transactionalId, paymentDate);
        }
    }
}
