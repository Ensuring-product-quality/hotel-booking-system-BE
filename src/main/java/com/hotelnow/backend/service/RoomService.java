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

import com.hotelnow.backend.entity.User;
import com.hotelnow.backend.entity.Role;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.web.multipart.MultipartFile;
import com.hotelnow.backend.entity.RoomImage;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomImageRepository roomImageRepository;
    private final CurrentUserService currentUserService;
    private final FileStorageService fileStorageService;

    public RoomService(RoomRepository roomRepository,
                       HotelRepository hotelRepository,
                       RoomImageRepository roomImageRepository,
                       CurrentUserService currentUserService,
                       FileStorageService fileStorageService) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.roomImageRepository = roomImageRepository;
        this.currentUserService = currentUserService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public PageResponse<RoomResponseDTO> searchRooms(
            Long hotelId, String status, String keyword, Long managerId, Pageable pageable) {
        Page<Room> page = roomRepository.searchRooms(
                hotelId, normalizeStatus(status, true), normalize(keyword), managerId, pageable);
        return PageResponse.fromPage(page.map(this::mapToRoomResponse));
    }

    @Transactional(readOnly = true)
    public RoomDetailDTO getRoomDetail(Long roomId) {
        Room room = findRoom(roomId);
        List<String> images = roomImageRepository.findByRoomIdOrderByIdDesc(roomId).stream()
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

    private void checkManagerPermission(Hotel hotel) {
        User current = currentUserService.requireCurrentUser();
        if (current.getRole() == Role.MANAGER) {
            User manager = hotel.getManager();
            if (manager == null || !manager.getId().equals(current.getId())) {
                throw new AccessDeniedException("Bạn không có quyền quản lý khách sạn này");
            }
        }
    }

    @Transactional
    public RoomResponseDTO createRoom(RoomCreateDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn tương ứng"));
        checkManagerPermission(hotel);
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
        checkManagerPermission(room.getHotel());
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
        Room room = findRoom(roomId);
        checkManagerPermission(room.getHotel());
        roomRepository.delete(room);
    }

    private Room findRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin phòng"));
    }

    private RoomType parseType(String value) {
        try {
            return RoomType.valueOf(value.toUpperCase());
        } catch (RuntimeException ex) {
            throw new BadRequestException(
                    "Invalid room type. Choose SINGLE, DOUBLE, SUITE, or DELUXE");
        }
    }

    @Transactional
    public RoomResponseDTO updateRoomStatus(Long roomId, String status) {
        Room room = findRoom(roomId);
        checkManagerPermission(room.getHotel());
        room.setStatus(normalizeStatus(status, false));
        return mapToRoomResponse(roomRepository.save(room));
    }

    private String normalizeStatus(String value, boolean optional) {
        if (value == null || value.isBlank()) {
            if (optional) {
                return null;
            }
            throw new BadRequestException("Trạng thái không được để trống");
        }
        String status = value.trim().toLowerCase();
        List<String> validStatuses = List.of("active", "available", "occupied", "cleaning", "maintenance", "inactive");
        if (!validStatuses.contains(status)) {
            throw new BadRequestException("Trạng thái phòng phải là: active/available, occupied, cleaning, maintenance, hoặc inactive");
        }
        return status;
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private RoomResponseDTO mapToRoomResponse(Room room) {
        List<String> images = roomImageRepository.findByRoomIdOrderByIdDesc(room.getId()).stream()
                .map(image -> image.getImageUrl())
                .toList();
        return RoomResponseDTO.builder()
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
    public String uploadImage(Long roomId, MultipartFile file) {
        Room room = findRoom(roomId);
        checkManagerPermission(room.getHotel());
        String imageUrl = fileStorageService.storeImage(file, "rooms");
        RoomImage roomImage = RoomImage.builder()
                .room(room)
                .imageUrl(imageUrl)
                .build();
        roomImageRepository.save(roomImage);
        return imageUrl;
    }
}
