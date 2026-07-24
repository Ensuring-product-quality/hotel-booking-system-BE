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

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<HotelResponseDTO>>> getHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer stars,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {

        Pageable pageable = PageableFactory.create(page, size, sort, Set.of("id", "name", "city", "stars", "averageRating", "price", "createdAt", "updatedAt"));

        PageResponse<HotelResponseDTO> data = hotelService.searchHotels(city, stars, keyword, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelDetailDTO>> getHotelById(@PathVariable Long hotelId) {
        HotelDetailDTO data = hotelService.getHotelDetail(hotelId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{hotelId}/search")
    public ResponseEntity<ApiResponse<PageResponse<HotelResponseDTO>>> searchHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer stars,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {

        Pageable pageable = PageableFactory.create(page, size, sort, Set.of("id", "name", "city", "stars", "averageRating", "price", "createdAt", "updatedAt"));

        PageResponse<HotelResponseDTO> data = hotelService.searchHotels(city, stars, keyword, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<HotelResponseDTO>> createHotel(@Valid @RequestBody HotelCreateDTO createDTO) {
        HotelResponseDTO data = hotelService.createHotel(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hotel created successfully", data, HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PutMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelResponseDTO>> updateHotel(
            @PathVariable Long hotelId,
            @Valid @RequestBody HotelCreateDTO updateDTO) {
        HotelResponseDTO data = hotelService.updateHotel(hotelId, updateDTO);
        return ResponseEntity.ok(ApiResponse.success("Hotel updated successfully", data, HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<Void>> deleteHotel(@PathVariable Long hotelId) {
        hotelService.deleteHotel(hotelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Hotel deleted successfully", null, HttpStatus.NO_CONTENT.value()));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/{hotelId}/images")
    public ResponseEntity<ApiResponse<String>> uploadHotelImage(
            @PathVariable Long hotelId,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = hotelService.uploadImage(hotelId, file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", imageUrl, HttpStatus.OK.value()));
    }
}
