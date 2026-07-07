package com.hotelnow.backend.service;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.entity.*;
import com.hotelnow.backend.exception.BadRequestException;
import com.hotelnow.backend.exception.BookingConflictException;
import com.hotelnow.backend.exception.ResourceNotFoundException;
import com.hotelnow.backend.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          UserRepository userRepository,
                          PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public BookingResponseDTO createBooking(BookingCreateDTO createDTO) {
        if (!createDTO.getCheckOutDate().isAfter(createDTO.getCheckInDate())) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }

        User user = userRepository.findById(createDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Step 1: Pessimistic locking of the room to prevent race conditions (double-booking)
        Room room = roomRepository.findByIdForUpdate(createDTO.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (!"active".equalsIgnoreCase(room.getStatus())) {
            throw new BadRequestException("Room is not active for booking");
        }

        // Step 2: Check for overlapping bookings
        List<BookingStatus> activeStatuses = Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.PENDING_PAYMENT);
        boolean overlap = bookingRepository.hasOverlappingBookings(
                room.getId(),
                createDTO.getCheckInDate(),
                createDTO.getCheckOutDate(),
                activeStatuses
        );

        if (overlap) {
            throw new BookingConflictException("The room is already booked for the selected dates");
        }

        // Step 3: Price calculation (room price * nights * 1.15 tax/service fees)
        long nights = ChronoUnit.DAYS.between(createDTO.getCheckInDate(), createDTO.getCheckOutDate());
        BigDecimal rawPrice = room.getPrice().multiply(BigDecimal.valueOf(nights));
        BigDecimal taxAndFeesMultiplier = BigDecimal.valueOf(1.15);
        BigDecimal totalPrice = rawPrice.multiply(taxAndFeesMultiplier);

        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .checkInDate(createDTO.getCheckInDate())
                .checkOutDate(createDTO.getCheckOutDate())
                .guests(createDTO.getGuests())
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING_PAYMENT)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        return mapToBookingResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookingResponseDTO> searchBookings(Long userId, String statusStr, String keyword, Pageable pageable) {
        BookingStatus status = null;
        if (statusStr != null) {
            try {
                status = BookingStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore or handle
            }
        }

        Page<Booking> bookingsPage = bookingRepository.searchBookings(userId, status, keyword, pageable);
        return PageResponse.fromPage(bookingsPage.map(this::mapToBookingResponse));
    }

    @Transactional(readOnly = true)
    public BookingDetailDTO getBookingDetail(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        String paymentStatus = paymentRepository.findByBookingId(bookingId)
                .map(p -> p.getStatus().name().toLowerCase())
                .orElse("pending");

        return BookingDetailDTO.builder()
                .id(booking.getId())
                .user(mapToUserResponse(booking.getUser()))
                .room(mapToRoomResponse(booking.getRoom()))
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .guests(booking.getGuests())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name().toLowerCase())
                .paymentStatus(paymentStatus)
                .build();
    }

    @Transactional
    public BookingResponseDTO updateBooking(Long bookingId, BookingUpdateDTO updateDTO) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!updateDTO.getCheckOutDate().isAfter(updateDTO.getCheckInDate())) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }

        // Recheck overlap if dates changed
        if (!booking.getCheckInDate().equals(updateDTO.getCheckInDate()) ||
            !booking.getCheckOutDate().equals(updateDTO.getCheckOutDate())) {
            
            List<BookingStatus> activeStatuses = Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.PENDING_PAYMENT);
            Room room = roomRepository.findByIdForUpdate(booking.getRoom().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

            boolean actuallyOverlaps = bookingRepository.findAll().stream()
                    .filter(b -> !b.getId().equals(bookingId))
                    .filter(b -> b.getRoom().getId().equals(room.getId()))
                    .filter(b -> activeStatuses.contains(b.getStatus()))
                    .anyMatch(b -> b.getCheckInDate().isBefore(updateDTO.getCheckOutDate()) && 
                                   b.getCheckOutDate().isAfter(updateDTO.getCheckInDate()));

            if (actuallyOverlaps) {
                throw new BookingConflictException("The room is already booked for the selected dates");
            }

            long nights = ChronoUnit.DAYS.between(updateDTO.getCheckInDate(), updateDTO.getCheckOutDate());
            BigDecimal rawPrice = room.getPrice().multiply(BigDecimal.valueOf(nights));
            booking.setTotalPrice(rawPrice.multiply(BigDecimal.valueOf(1.15)));
        }

        booking.setCheckInDate(updateDTO.getCheckInDate());
        booking.setCheckOutDate(updateDTO.getCheckOutDate());
        booking.setGuests(updateDTO.getGuests());

        try {
            booking.setStatus(BookingStatus.valueOf(updateDTO.getStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid booking status");
        }

        Booking savedBooking = bookingRepository.save(booking);
        return mapToBookingResponse(savedBooking);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public boolean checkAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            return false;
        }
        List<BookingStatus> activeStatuses = Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.PENDING_PAYMENT);
        return !bookingRepository.hasOverlappingBookings(roomId, checkIn, checkOut, activeStatuses);
    }

    @Transactional(readOnly = true)
    public String getPaymentStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return paymentRepository.findByBookingId(bookingId)
                .map(p -> p.getStatus().name().toLowerCase())
                .orElse("pending");
    }

    private BookingResponseDTO mapToBookingResponse(Booking booking) {
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .roomId(booking.getRoom().getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .guests(booking.getGuests())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name().toLowerCase())
                .build();
    }

    private UserResponseDTO mapToUserResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus())
                .build();
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
