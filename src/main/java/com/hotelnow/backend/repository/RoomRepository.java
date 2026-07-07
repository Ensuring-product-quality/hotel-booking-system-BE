package com.hotelnow.backend.repository;

import com.hotelnow.backend.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT r FROM Room r WHERE " +
            "(:hotelId IS NULL OR r.hotel.id = :hotelId) AND " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:keyword IS NULL OR LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Room> searchRooms(@Param("hotelId") Long hotelId,
                           @Param("status") String status,
                           @Param("keyword") String keyword,
                           Pageable pageable);
}
