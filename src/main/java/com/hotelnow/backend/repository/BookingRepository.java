package com.hotelnow.backend.repository;

import com.hotelnow.backend.entity.Booking;
import com.hotelnow.backend.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.status IN :activeStatuses " +
            "AND b.checkInDate < :checkOutDate " +
            "AND b.checkOutDate > :checkInDate")
    boolean hasOverlappingBookings(@Param("roomId") Long roomId,
                                   @Param("checkInDate") LocalDate checkInDate,
                                   @Param("checkOutDate") LocalDate checkOutDate,
                                   @Param("activeStatuses") List<BookingStatus> activeStatuses);

    @Query("SELECT b FROM Booking b WHERE " +
            "(:userId IS NULL OR b.user.id = :userId) AND " +
            "(:status IS NULL OR b.status = :status) AND " +
            "(:keyword IS NULL OR LOWER(b.room.roomNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.room.hotel.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Booking> searchBookings(@Param("userId") Long userId,
                                 @Param("status") BookingStatus status,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);

    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime dateTime);
}
