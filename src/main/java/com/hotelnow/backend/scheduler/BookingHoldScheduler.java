package com.hotelnow.backend.scheduler;

import com.hotelnow.backend.entity.Booking;
import com.hotelnow.backend.entity.BookingStatus;
import com.hotelnow.backend.repository.BookingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingHoldScheduler {

    private final BookingRepository bookingRepository;

    public BookingHoldScheduler(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Scheduled(cron = "0 * * * * *") // Runs every minute
    @Transactional
    public void releaseExpiredPendingBookings() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);
        List<Booking> expiredBookings = bookingRepository.findByStatusAndCreatedAtBefore(
                BookingStatus.PENDING_PAYMENT,
                threshold
        );

        if (!expiredBookings.isEmpty()) {
            for (Booking booking : expiredBookings) {
                booking.setStatus(BookingStatus.CANCELLED);
            }
            bookingRepository.saveAll(expiredBookings);
        }
    }
}
