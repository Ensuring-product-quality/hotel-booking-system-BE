package com.hotelnow.backend.repository;

import com.hotelnow.backend.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    // Base filter - lọc giá qua min price của Room active và kiểm tra phòng trống theo check-in/check-out
    String FILTER = "(:city IS NULL OR LOWER(:city) LIKE LOWER(CONCAT('%', h.city, '%')) OR LOWER(h.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:stars IS NULL OR h.stars = :stars) AND " +
            "(:status IS NULL OR h.status = :status) AND " +
            "(:managerId IS NULL OR h.manager.id = :managerId) AND " +
            "(:keyword IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(h.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:minPrice IS NULL OR EXISTS (SELECT r FROM Room r WHERE r.hotel = h AND r.status = 'active' AND r.price >= :minPrice)) AND " +
            "(:maxPrice IS NULL OR EXISTS (SELECT r FROM Room r WHERE r.hotel = h AND r.status = 'active' AND r.price <= :maxPrice)) AND " +
            "(:checkInDate IS NULL OR :checkOutDate IS NULL OR EXISTS (SELECT r FROM Room r WHERE r.hotel = h AND r.status = 'active' AND NOT EXISTS (SELECT b FROM Booking b WHERE b.room = r AND b.status IN (com.hotelnow.backend.entity.BookingStatus.CONFIRMED, com.hotelnow.backend.entity.BookingStatus.PENDING_PAYMENT, com.hotelnow.backend.entity.BookingStatus.CHECKED_IN) AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate)))";

    @Query("SELECT h FROM Hotel h WHERE " + FILTER)
    Page<Hotel> searchHotels(@Param("city") String city,
                             @Param("stars") Integer stars,
                             @Param("keyword") String keyword,
                             @Param("status") String status,
                             @Param("managerId") Long managerId,
                             @Param("minPrice") java.math.BigDecimal minPrice,
                             @Param("maxPrice") java.math.BigDecimal maxPrice,
                             @Param("checkInDate") java.time.LocalDate checkInDate,
                             @Param("checkOutDate") java.time.LocalDate checkOutDate,
                             Pageable pageable);

    @Query(
            value = "SELECT h FROM Hotel h LEFT JOIN Room r ON r.hotel = h " +
                    "WHERE " + FILTER + " GROUP BY h " +
                    "ORDER BY MIN(CASE WHEN r.status = 'active' THEN r.price ELSE NULL END) ASC, h.id ASC",
            countQuery = "SELECT COUNT(h) FROM Hotel h WHERE " + FILTER)
    Page<Hotel> searchHotelsByLowestPriceAsc(
            @Param("city") String city,
            @Param("stars") Integer stars,
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("managerId") Long managerId,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            @Param("checkInDate") java.time.LocalDate checkInDate,
            @Param("checkOutDate") java.time.LocalDate checkOutDate,
            Pageable pageable);

    @Query(
            value = "SELECT h FROM Hotel h LEFT JOIN Room r ON r.hotel = h " +
                    "WHERE " + FILTER + " GROUP BY h " +
                    "ORDER BY MIN(CASE WHEN r.status = 'active' THEN r.price ELSE NULL END) DESC, h.id ASC",
            countQuery = "SELECT COUNT(h) FROM Hotel h WHERE " + FILTER)
    Page<Hotel> searchHotelsByLowestPriceDesc(
            @Param("city") String city,
            @Param("stars") Integer stars,
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("managerId") Long managerId,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            @Param("checkInDate") java.time.LocalDate checkInDate,
            @Param("checkOutDate") java.time.LocalDate checkOutDate,
            Pageable pageable);
}
