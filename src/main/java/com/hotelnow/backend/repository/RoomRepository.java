package com.hotelnow.backend.repository;

import com.hotelnow.backend.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);

    boolean existsByHotelIdAndRoomNumberIgnoreCase(Long hotelId, String roomNumber);

    boolean existsByHotelIdAndRoomNumberIgnoreCaseAndIdNot(
            Long hotelId, String roomNumber, Long roomId);

    Optional<Room> findFirstByHotelIdAndStatusOrderByPriceAsc(Long hotelId, String status);

    @Query("SELECT r FROM Room r WHERE " +
            "(:hotelId IS NULL OR r.hotel.id = :hotelId) AND " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:managerId IS NULL OR r.hotel.manager.id = :managerId) AND " +
            "(:keyword IS NULL OR LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Room> searchRooms(@Param("hotelId") Long hotelId,
                           @Param("status") String status,
                           @Param("keyword") String keyword,
                           @Param("managerId") Long managerId,
                           Pageable pageable);
}
