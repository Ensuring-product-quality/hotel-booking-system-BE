package com.hotelnow.backend.controller;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RoomResponseDTO>>> getRooms(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "roomNumber,asc") String sort) {

        String[] sortParams = sort.split(",");
        Sort sorting = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        PageResponse<RoomResponseDTO> data = roomService.searchRooms(hotelId, status, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomDetailDTO>> getRoomById(@PathVariable Long roomId) {
        RoomDetailDTO data = roomService.getRoomDetail(roomId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{roomId}/images")
    public ResponseEntity<ApiResponse<List<String>>> getRoomImages(@PathVariable Long roomId) {
        RoomDetailDTO data = roomService.getRoomDetail(roomId);
        return ResponseEntity.ok(ApiResponse.success(data.getImages() != null ? data.getImages() : Collections.emptyList()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponseDTO>> createRoom(@Valid @RequestBody RoomCreateDTO createDTO) {
        RoomResponseDTO data = roomService.createRoom(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created successfully", data, HttpStatus.CREATED.value()));
    }

    @PutMapping("/{roomId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponseDTO>> updateRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody RoomUpdateDTO updateDTO) {
        RoomResponseDTO data = roomService.updateRoom(roomId, updateDTO);
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", data, HttpStatus.OK.value()));
    }

    @DeleteMapping("/{roomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success("Room deleted successfully", null, HttpStatus.NO_CONTENT.value()));
    }
}
