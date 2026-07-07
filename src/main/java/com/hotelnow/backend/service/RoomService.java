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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    public PageResponse<RoomResponseDTO> searchRooms(Long hotelId, String status, String keyword, Pageable pageable) {
        Page<Room> page = roomRepository.searchRooms(hotelId, status, keyword, pageable);
        return PageResponse.fromPage(page.map(this::mapToRoomResponse));
    }

    @Transactional(readOnly = true)
    public RoomDetailDTO getRoomDetail(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        List<String> images = roomImageRepository.findByRoomId(roomId).stream()
                .map(img -> img.getImageUrl())
                .collect(Collectors.toList());

        return RoomDetailDTO.builder()
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType().name())
                .price(room.getPrice())
                .description(room.getDescription())
                .status(room.getStatus())
                .images(images)
                .build();
    }

    @Transactional
    public RoomResponseDTO createRoom(RoomCreateDTO createDTO) {
        Hotel hotel = hotelRepository.findById(createDTO.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel associated not found"));

        RoomType type;
        try {
            type = RoomType.valueOf(createDTO.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid room type. Choose SINGLE, DOUBLE, SUITE, or DELUXE");
        }

        Room room = Room.builder()
                .hotel(hotel)
                .roomNumber(createDTO.getRoomNumber())
                .type(type)
                .price(createDTO.getPrice())
                .description(createDTO.getDescription())
                .status(createDTO.getStatus())
                .build();

        Room savedRoom = roomRepository.save(room);
        return mapToRoomResponse(savedRoom);
    }

    @Transactional
    public RoomResponseDTO updateRoom(Long roomId, RoomUpdateDTO updateDTO) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        RoomType type;
        try {
            type = RoomType.valueOf(updateDTO.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid room type. Choose SINGLE, DOUBLE, SUITE, or DELUXE");
        }

        room.setRoomNumber(updateDTO.getRoomNumber());
        room.setType(type);
        room.setPrice(updateDTO.getPrice());
        room.setDescription(updateDTO.getDescription());
        room.setStatus(updateDTO.getStatus());

        Room savedRoom = roomRepository.save(room);
        return mapToRoomResponse(savedRoom);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        roomRepository.delete(room);
    }

    private RoomResponseDTO mapToRoomResponse(Room room) {
        return RoomResponseDTO.builder()
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType().name())
                .price(room.getPrice())
                .description(room.getDescription())
                .status(room.getStatus())
                .build();
    }
}
