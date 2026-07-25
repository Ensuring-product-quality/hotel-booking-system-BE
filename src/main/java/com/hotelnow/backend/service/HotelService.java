package com.hotelnow.backend.service;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.entity.Hotel;
import com.hotelnow.backend.exception.ResourceNotFoundException;
import com.hotelnow.backend.repository.HotelRepository;
import com.hotelnow.backend.repository.RoomImageRepository;
import com.hotelnow.backend.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final FileStorageService fileStorageService;

    public HotelService(HotelRepository hotelRepository, RoomRepository roomRepository,
                        RoomImageRepository roomImageRepository,
                        FileStorageService fileStorageService) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.roomImageRepository = roomImageRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public PageResponse<HotelResponseDTO> searchHotels(String city, Integer stars, String keyword, String status, Pageable pageable) {
        Page<Hotel> hotelsPage;
        Sort.Order priceOrder = pageable.getSort().getOrderFor("price");
        if (priceOrder != null) {
            Pageable pricePage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            hotelsPage = priceOrder.isAscending()
                    ? hotelRepository.searchHotelsByLowestPriceAsc(city, stars, keyword, status, pricePage)
                    : hotelRepository.searchHotelsByLowestPriceDesc(city, stars, keyword, status, pricePage);
        } else {
            hotelsPage = hotelRepository.searchHotels(city, stars, keyword, status, pageable);
        }
        return PageResponse.fromPage(hotelsPage.map(this::mapToHotelResponse));
    }

    @Transactional(readOnly = true)
    public HotelDetailDTO getHotelDetail(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        List<RoomResponseDTO> rooms = roomRepository.findAll().stream()
                .filter(r -> r.getHotel().getId().equals(hotelId))
                .map(r -> {
                    List<String> roomImages = roomImageRepository.findByRoomId(r.getId()).stream()
                            .map(img -> img.getImageUrl())
                            .toList();
                    return RoomResponseDTO.builder()
                            .id(r.getId())
                            .hotelId(hotelId)
                            .roomNumber(r.getRoomNumber())
                            .type(r.getType().name())
                            .price(r.getPrice())
                            .capacity(r.getCapacity())
                            .description(r.getDescription())
                            .status(r.getStatus())
                            .images(roomImages)
                            .build();
                })
                .collect(Collectors.toList());

        return HotelDetailDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .address(hotel.getAddress())
                .city(hotel.getCity())
                .stars(hotel.getStars())
                .description(hotel.getDescription())
                .status(hotel.getStatus())
                .averageRating(hotel.getAverageRating())
                .images(toImages(hotel.getImageUrl()))
                .rooms(rooms)
                .build();
    }

    @Transactional
    public HotelResponseDTO createHotel(HotelCreateDTO createDTO) {
        Hotel hotel = Hotel.builder()
                .name(createDTO.getName())
                .address(createDTO.getAddress())
                .city(createDTO.getCity())
                .stars(createDTO.getStars())
                .description(createDTO.getDescription())
                .status(createDTO.getStatus())
                .build();

        Hotel savedHotel = hotelRepository.save(hotel);
        return mapToHotelResponse(savedHotel);
    }

    @Transactional
    public HotelResponseDTO updateHotel(Long hotelId, HotelCreateDTO updateDTO) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        hotel.setName(updateDTO.getName());
        hotel.setAddress(updateDTO.getAddress());
        hotel.setCity(updateDTO.getCity());
        hotel.setStars(updateDTO.getStars());
        hotel.setDescription(updateDTO.getDescription());
        hotel.setStatus(updateDTO.getStatus());

        Hotel savedHotel = hotelRepository.save(hotel);
        return mapToHotelResponse(savedHotel);
    }

    @Transactional
    public void deleteHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        hotelRepository.delete(hotel);
    }

    @Transactional
    public String uploadImage(Long hotelId, MultipartFile file) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        String imageUrl = fileStorageService.storeImage(file, "hotels");
        hotel.setImageUrl(imageUrl);
        hotelRepository.save(hotel);
        return imageUrl;
    }

    private HotelResponseDTO mapToHotelResponse(Hotel hotel) {
        return HotelResponseDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .address(hotel.getAddress())
                .city(hotel.getCity())
                .stars(hotel.getStars())
                .description(hotel.getDescription())
                .status(hotel.getStatus())
                .averageRating(hotel.getAverageRating())
                .price(roomRepository
                        .findFirstByHotelIdAndStatusOrderByPriceAsc(hotel.getId(), "active")
                        .map(room -> room.getPrice()).orElse(null))
                .images(toImages(hotel.getImageUrl()))
                .build();
    }

    private List<String> toImages(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(imageUrl.split(","))
                .map(String::trim)
                .toList();
    }
}
