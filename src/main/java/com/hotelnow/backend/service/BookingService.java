package com.hotelnow.backend.service;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.entity.*;
import com.hotelnow.backend.exception.BadRequestException;
import com.hotelnow.backend.exception.BookingConflictException;
import com.hotelnow.backend.exception.ResourceNotFoundException;
import com.hotelnow.backend.repository.BookingRepository;
import com.hotelnow.backend.repository.PaymentRepository;
import com.hotelnow.backend.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class BookingService {

    private static final BigDecimal TAX_AND_FEES_MULTIPLIER = new BigDecimal("1.15");

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final CurrentUserService currentUserService;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          PaymentRepository paymentRepository,
                          CurrentUserService currentUserService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.paymentRepository = paymentRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public BookingResponseDTO createBooking(BookingCreateDTO createDTO) {
        validateDates(createDTO.getCheckInDate(), createDTO.getCheckOutDate());
        User user = currentUserService.requireCurrentUser();

        Room room = roomRepository.findByIdForUpdate(createDTO.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin phòng"));
        if ("inactive".equalsIgnoreCase(room.getStatus()) || "maintenance".equalsIgnoreCase(room.getStatus())) {
            throw new BadRequestException("Phòng hiện không ở trạng thái sẵn sàng để đặt");
        }
        if (createDTO.getGuests() > room.getCapacity()) {
            throw new BadRequestException("Số lượng khách vượt quá sức chứa tối đa của phòng (" + room.getCapacity() + " người)");
        }

        List<BookingStatus> activeStatuses =
                Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.PENDING_PAYMENT);
        if (bookingRepository.hasOverlappingBookings(
                room.getId(), createDTO.getCheckInDate(), createDTO.getCheckOutDate(), activeStatuses)) {
            throw new BookingConflictException("Phòng đã được đặt trong khoảng thời gian bạn chọn");
        }

        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .checkInDate(createDTO.getCheckInDate())
                .checkOutDate(createDTO.getCheckOutDate())
                .guests(createDTO.getGuests())
                .totalPrice(calculateTotal(room, createDTO.getCheckInDate(), createDTO.getCheckOutDate()))
                .status(BookingStatus.PENDING_PAYMENT)
                .build();
        return mapToBookingResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public PageResponse<BookingResponseDTO> searchBookings(
            Long requestedUserId, String statusValue, String keyword, Pageable pageable) {
        User current = currentUserService.requireCurrentUser();
        Long effectiveUserId = currentUserService.isStaff(current) ? requestedUserId : current.getId();
        BookingStatus status = parseStatus(statusValue);
        Long managerId = (current.getRole() == Role.MANAGER) ? current.getId() : null;
        Page<Booking> page = bookingRepository.searchBookings(effectiveUserId, status, normalize(keyword), managerId, pageable);
        return PageResponse.fromPage(page.map(this::mapToBookingResponse));
    }

    @Transactional(readOnly = true)
    public BookingDetailDTO getBookingDetail(Long bookingId) {
        Booking booking = findAccessibleBooking(bookingId);
        return mapToBookingDetail(booking);
    }

    @Transactional(readOnly = true)
    public BookingDetailDTO publicLookup(Long bookingId, String email) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn đặt phòng"));
        if (email == null || !booking.getUser().getEmail().equalsIgnoreCase(email.trim())) {
            throw new ResourceNotFoundException("Không tìm thấy đơn đặt phòng");
        }
        return mapToBookingDetail(booking);
    }

    @Transactional
    public BookingResponseDTO updateBooking(Long bookingId, BookingUpdateDTO updateDTO) {
        Booking booking = findAccessibleBooking(bookingId);
        User current = currentUserService.requireCurrentUser();

        if (!currentUserService.isStaff(current) && booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new BadRequestException("Khách hàng chỉ có thể chỉnh sửa đơn đặt phòng đang chờ thanh toán");
        }

        if (!currentUserService.isStaff(current)
                && updateDTO.getStatus() != null
                && !updateDTO.getStatus().isBlank()) {
            throw new BadRequestException("Khách hàng không thể tự thay đổi trạng thái đơn đặt phòng");
        }

        LocalDate nextCheckIn = updateDTO.getCheckInDate() != null
                ? updateDTO.getCheckInDate() : booking.getCheckInDate();
        LocalDate nextCheckOut = updateDTO.getCheckOutDate() != null
                ? updateDTO.getCheckOutDate() : booking.getCheckOutDate();
        Integer nextGuests = updateDTO.getGuests() != null
                ? updateDTO.getGuests() : booking.getGuests();
        validateDates(nextCheckIn, nextCheckOut);

        boolean datesChanged = !booking.getCheckInDate().equals(nextCheckIn)
                || !booking.getCheckOutDate().equals(nextCheckOut);
        boolean guestsChanged = !booking.getGuests().equals(nextGuests);
        if (datesChanged || guestsChanged) {
            Room room = roomRepository.findByIdForUpdate(booking.getRoom().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin phòng"));
            if (!"active".equalsIgnoreCase(room.getStatus()) && !"available".equalsIgnoreCase(room.getStatus())) {
                throw new BadRequestException("Phòng hiện không ở trạng thái sẵn sàng để đặt");
            }
            if (nextGuests > room.getCapacity()) {
                throw new BadRequestException("Số lượng khách vượt quá sức chứa tối đa của phòng (" + room.getCapacity() + " người)");
            }

            List<BookingStatus> activeStatuses =
                    Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.PENDING_PAYMENT);
            if (bookingRepository.hasOverlappingBookingsExcluding(
                    bookingId, room.getId(), nextCheckIn, nextCheckOut, activeStatuses)) {
                throw new BookingConflictException("Phòng đã được đặt trong khoảng thời gian bạn chọn");
            }
            if (datesChanged) {
                booking.setTotalPrice(calculateTotal(room, nextCheckIn, nextCheckOut));
            }
        }

        booking.setCheckInDate(nextCheckIn);
        booking.setCheckOutDate(nextCheckOut);
        booking.setGuests(nextGuests);

        if (currentUserService.isStaff(current)
                && updateDTO.getStatus() != null
                && !updateDTO.getStatus().isBlank()) {
            BookingStatus nextStatus = parseRequiredStatus(updateDTO.getStatus());
            requireValidTransition(booking.getStatus(), nextStatus);
            booking.setStatus(nextStatus);
        }
        return mapToBookingResponse(bookingRepository.save(booking));
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = findAccessibleBooking(bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Đơn đặt phòng này đã bị hủy trước đó");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Không thể hủy đơn đặt phòng đã hoàn thành lưu trú");
        }

        paymentRepository.findByBookingId(bookingId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
            }
        });
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Transactional
    public BookingResponseDTO checkInBooking(Long bookingId) {
        Booking booking = findAccessibleBooking(bookingId);
        
        // Nếu check-in sớm hơn ngày đặt dự kiến, cập nhật ngày nhận phòng thực tế thành ngày hôm nay
        LocalDate today = LocalDate.now();
        if (today.isBefore(booking.getCheckInDate())) {
            booking.setCheckInDate(today);
            // Tính toán lại tổng tiền dựa trên số đêm thực tế mới
            long nights = java.time.temporal.ChronoUnit.DAYS.between(today, booking.getCheckOutDate());
            if (nights > 0) {
                booking.setTotalPrice(booking.getRoom().getPrice().multiply(java.math.BigDecimal.valueOf(nights)));
            }
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        Room room = booking.getRoom();
        room.setStatus("occupied");
        roomRepository.save(room);
        return mapToBookingResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponseDTO checkOutBooking(Long bookingId) {
        Booking booking = findAccessibleBooking(bookingId);
        booking.setStatus(BookingStatus.CHECKED_OUT);
        Room room = booking.getRoom();
        room.setStatus("cleaning");
        roomRepository.save(room);
        return mapToBookingResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public boolean checkAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin phòng"));
        List<BookingStatus> activeStatuses =
                Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.PENDING_PAYMENT, BookingStatus.CHECKED_IN);
        return !bookingRepository.hasOverlappingBookings(roomId, checkIn, checkOut, activeStatuses);
    }

    @Transactional(readOnly = true)
    public String getPaymentStatus(Long bookingId) {
        findAccessibleBooking(bookingId);
        return paymentRepository.findByBookingId(bookingId)
                .map(payment -> payment.getStatus().name().toLowerCase())
                .orElse("pending");
    }

    private Booking findAccessibleBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn đặt phòng"));
        User current = currentUserService.requireCurrentUser();
        if (!booking.getUser().getId().equals(current.getId()) && !currentUserService.isStaff(current)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập đơn đặt phòng của người dùng khác");
        }
        if (current.getRole() == Role.MANAGER) {
            User manager = booking.getRoom().getHotel().getManager();
            if (manager == null || !manager.getId().equals(current.getId())) {
                throw new AccessDeniedException("Bạn chỉ có quyền truy cập đơn đặt phòng thuộc khách sạn mà mình quản lý");
            }
        }
        return booking;
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new BadRequestException("Ngày trả phòng (Check-out) phải sau ngày nhận phòng (Check-in)");
        }
    }

    private BigDecimal calculateTotal(Room room, LocalDate checkIn, LocalDate checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        return room.getPrice()
                .multiply(BigDecimal.valueOf(nights))
                .multiply(TAX_AND_FEES_MULTIPLIER);
    }

    private BookingStatus parseStatus(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return parseRequiredStatus(value);
    }

    private BookingStatus parseRequiredStatus(String value) {
        try {
            return BookingStatus.valueOf(value.toUpperCase());
        } catch (RuntimeException ex) {
            throw new BadRequestException("Trạng thái đơn đặt phòng không hợp lệ");
        }
    }

    private void requireValidTransition(BookingStatus current, BookingStatus next) {
        if (current == next) {
            return;
        }
        boolean valid = switch (current) {
            case PENDING_PAYMENT -> next == BookingStatus.CONFIRMED || next == BookingStatus.CANCELLED;
            case CONFIRMED -> next == BookingStatus.CHECKED_IN || next == BookingStatus.CANCELLED || next == BookingStatus.COMPLETED;
            case CHECKED_IN -> next == BookingStatus.CHECKED_OUT || next == BookingStatus.CANCELLED || next == BookingStatus.COMPLETED;
            case CHECKED_OUT -> next == BookingStatus.COMPLETED || next == BookingStatus.CANCELLED;
            case CANCELLED, COMPLETED -> false;
        };
        if (!valid) {
            throw new BadRequestException("Chuyển đổi trạng thái đơn không hợp lệ: " + current + " -> " + next);
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private BookingDetailDTO mapToBookingDetail(Booking booking) {
        String paymentStatus = paymentRepository.findByBookingId(booking.getId())
                .map(payment -> payment.getStatus().name().toLowerCase())
                .orElse("pending");
        return BookingDetailDTO.builder()
                .id(booking.getId())
                .user(mapToUserResponse(booking.getUser()))
                .room(mapToRoomResponse(booking.getRoom()))
                .hotelId(booking.getRoom().getHotel().getId())
                .hotelName(booking.getRoom().getHotel().getName())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .guests(booking.getGuests())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name().toLowerCase())
                .paymentStatus(paymentStatus)
                .userFullName(booking.getUser().getFullName())
                .userPhone(booking.getUser().getPhone())
                .build();
    }

    private BookingResponseDTO mapToBookingResponse(Booking booking) {
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .roomId(booking.getRoom().getId())
                .roomNumber(booking.getRoom().getRoomNumber())
                .hotelId(booking.getRoom().getHotel().getId())
                .hotelName(booking.getRoom().getHotel().getName())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .guests(booking.getGuests())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name().toLowerCase())
                .userFullName(booking.getUser().getFullName())
                .userPhone(booking.getUser().getPhone())
                .build();
    }

    private UserResponseDTO mapToUserResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus())
                .avatarUrl(user.getAvatarUrl())
                .build();
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

    @Transactional(readOnly = true)
    public List<BookedDateRangeDTO> getBookedDatesForRoom(Long roomId) {
        List<BookingStatus> activeStatuses = Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.PENDING_PAYMENT, BookingStatus.CHECKED_IN);
        return bookingRepository.findByRoomIdAndStatusIn(roomId, activeStatuses).stream()
                .map(b -> new BookedDateRangeDTO(b.getCheckInDate(), b.getCheckOutDate()))
                .toList();
    }
}
