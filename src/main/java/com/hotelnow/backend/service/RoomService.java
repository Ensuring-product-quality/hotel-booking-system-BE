package com.hotelnow.backend.service;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.entity.Hotel;
import com.hotelnow.backend.entity.Room;
import com.hotelnow.backend.entity.RoomType;
import com.hotelnow.backend.exception.BadRequestException;
import com.hotelnow.backend.exception.ResourceNotFoundException;
import com.hotelnow.backend.repository.HotelRepository;
import com.hotelnow.backend.repository.RoomImageRepository;
import com.hotelnow.backend.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomImageRepository roomImageRepository;

    public RoomService(RoomRepository roomRepository,
                       HotelRepository hotelRepository,
                       RoomImageRepository roomImageRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.roomImageRepository = roomImageRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<RoomResponseDTO> searchRooms(
            Long hotelId, String status, String keyword, Pageable pageable) {
        Page<Room> page = roomRepository.searchRooms(
                hotelId, normalizeStatus(status, true), normalize(keyword), pageable);
        return PageResponse.fromPage(page.map(this::mapToRoomResponse));
    }

    @Transactional(readOnly = true)
    public RoomDetailDTO getRoomDetail(Long roomId) {
        Room room = findRoom(roomId);
        List<String> images = roomImageRepository.findByRoomId(roomId).stream()
                .map(image -> image.getImageUrl())
                .toList();
        return RoomDetailDTO.builder()
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType().name())
                .price(room.getPrice())
                .capacity(room.getCapacity())
                .description(room.getDescription())
                .status(room.getStatus())
                .images(images)
                .build();
    }

    @Transactional
    public RoomResponseDTO createRoom(RoomCreateDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel associated not found"));
        String number = dto.getRoomNumber().trim();
        if (roomRepository.existsByHotelIdAndRoomNumberIgnoreCase(hotel.getId(), number)) {
            throw new BadRequestException("Room number already exists in this hotel");
        }
        Room room = Room.builder()
                .hotel(hotel)
                .roomNumber(number)
                .type(parseType(dto.getType()))
                .price(dto.getPrice())
                .capacity(dto.getCapacity())
                .description(dto.getDescription())
                .status(normalizeStatus(dto.getStatus(), false))
                .build();
        return mapToRoomResponse(roomRepository.save(room));
    }

    @Transactional
    public RoomResponseDTO updateRoom(Long roomId, RoomUpdateDTO dto) {
        Room room = findRoom(roomId);
        String number = dto.getRoomNumber().trim();
        if (roomRepository.existsByHotelIdAndRoomNumberIgnoreCaseAndIdNot(
                room.getHotel().getId(), number, roomId)) {
            throw new BadRequestException("Room number already exists in this hotel");
        }
        room.setRoomNumber(number);
        room.setType(parseType(dto.getType()));
        room.setPrice(dto.getPrice());
        room.setCapacity(dto.getCapacity());
        room.setDescription(dto.getDescription());
        room.setStatus(normalizeStatus(dto.getStatus(), false));
        return mapToRoomResponse(roomRepository.save(room));
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        roomRepository.delete(findRoom(roomId));
    }

    private Room findRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    private RoomType parseType(String value) {
        try {
            return RoomType.valueOf(value.toUpperCase());
        } catch (RuntimeException ex) {
            throw new BadRequestException(
                    "Invalid room type. Choose SINGLE, DOUBLE, SUITE, or DELUXE");
        }
    }

    private String normalizeStatus(String value, boolean optional) {
        if (value == null || value.isBlank()) {
            if (optional) {
                return null;
            }
            throw new BadRequestException("Status is required");
        }
        String status = value.trim().toLowerCase();
        if (!status.equals("active") && !status.equals("inactive")) {
            throw new BadRequestException("Status must be active or inactive");
        }
        return status;
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private RoomResponseDTO mapToRoomResponse(Room room) {
        return RoomResponseDTO.builder()
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType().name())
                .price(room.getPrice())
                .capacity(room.getCapacity())
                .description(room.getDescription())
                .status(room.getStatus())
                .build();
    }
}
