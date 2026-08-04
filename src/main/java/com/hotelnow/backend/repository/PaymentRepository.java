package com.hotelnow.backend.repository;

import com.hotelnow.backend.entity.Payment;
import com.hotelnow.backend.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);

    @Query("SELECT p FROM Payment p WHERE " +
            "(:userId IS NULL OR p.booking.user.id = :userId) AND " +
            "(:bookingId IS NULL OR p.booking.id = :bookingId) AND " +
            "(:status IS NULL OR p.status = :status)")
    Page<Payment> searchPayments(@Param("userId") Long userId,
                                 @Param("bookingId") Long bookingId,
                                 @Param("status") PaymentStatus status,
                                 Pageable pageable);
}
