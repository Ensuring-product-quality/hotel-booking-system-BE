package com.hotelnow.backend.controller;

import java.util.Set;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.service.HotelService;
import com.hotelnow.backend.util.PageableFactory;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final com.hotelnow.backend.service.CurrentUserService currentUserService;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public HotelController(HotelService hotelService, com.hotelnow.backend.service.CurrentUserService currentUserService, org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.hotelService = hotelService;
        this.currentUserService = currentUserService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<HotelResponseDTO>>> getHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer stars,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate checkInDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate checkOutDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {

        Pageable pageable = PageableFactory.create(page, size, sort, Set.of("id", "name", "city", "stars", "averageRating", "price", "createdAt", "updatedAt"));

        Long managerId = null;
        try {
            com.hotelnow.backend.entity.User current = currentUserService.requireCurrentUser();
            if (current.getRole() == com.hotelnow.backend.entity.Role.MANAGER) {
                managerId = current.getId();
            }
        } catch (Exception ignored) {}

        PageResponse<HotelResponseDTO> data = hotelService.searchHotels(city, stars, keyword, status, managerId, minPrice, maxPrice, checkInDate, checkOutDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelDetailDTO>> getHotelById(@PathVariable Long hotelId) {
        HotelDetailDTO data = hotelService.getHotelDetail(hotelId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<HotelResponseDTO>>> searchHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer stars,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate checkInDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate checkOutDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {

        Pageable pageable = PageableFactory.create(page, size, sort, Set.of("id", "name", "city", "stars", "averageRating", "price", "createdAt", "updatedAt"));

        Long managerId = null;
        try {
            com.hotelnow.backend.entity.User current = currentUserService.requireCurrentUser();
            if (current.getRole() == com.hotelnow.backend.entity.Role.MANAGER) {
                managerId = current.getId();
            }
        } catch (Exception ignored) {}

        PageResponse<HotelResponseDTO> data = hotelService.searchHotels(city, stars, keyword, status, managerId, minPrice, maxPrice, checkInDate, checkOutDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<HotelResponseDTO>> createHotel(@Valid @RequestBody HotelCreateDTO createDTO) {
        HotelResponseDTO data = hotelService.createHotel(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo mới khách sạn thành công", data, HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PutMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelResponseDTO>> updateHotel(
            @PathVariable Long hotelId,
            @Valid @RequestBody HotelCreateDTO updateDTO) {
        HotelResponseDTO data = hotelService.updateHotel(hotelId, updateDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin khách sạn thành công", data, HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<Void>> deleteHotel(@PathVariable Long hotelId) {
        hotelService.deleteHotel(hotelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Xóa khách sạn thành công", null, HttpStatus.NO_CONTENT.value()));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/{hotelId}/images")
    public ResponseEntity<ApiResponse<String>> uploadHotelImage(
            @PathVariable Long hotelId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "replaceIndex", required = false) Integer replaceIndex) {
        String imageUrl = hotelService.uploadImage(hotelId, file, replaceIndex);
        return ResponseEntity.ok(ApiResponse.success("Tải lên hình ảnh thành công", imageUrl, HttpStatus.OK.value()));
    }

    @GetMapping("/diagnostic/db")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDbDiagnostics() {
        Map<String, Object> result = new HashMap<>();
        try {
            String dbName = jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName();
            result.put("database", dbName);
        } catch (Exception e) {
            result.put("database_error", e.getMessage());
        }

        try {
            Map<String, Object> bookingsStatus = jdbcTemplate.queryForMap(
                "SELECT DATA_TYPE, COLUMN_TYPE, CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'bookings' AND COLUMN_NAME = 'status'"
            );
            result.put("bookings_status_column", bookingsStatus);
        } catch (Exception e) {
            result.put("bookings_status_error", e.getMessage());
        }

        try {
            Map<String, Object> usersRole = jdbcTemplate.queryForMap(
                "SELECT DATA_TYPE, COLUMN_TYPE, CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'users' AND COLUMN_NAME = 'role'"
            );
            result.put("users_role_column", usersRole);
        } catch (Exception e) {
            result.put("users_role_error", e.getMessage());
        }

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
