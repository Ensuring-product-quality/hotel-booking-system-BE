package com.hotelnow.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingResponseDTO {
    private Long id;
    private Long userId;
    private Long roomId;
    private String roomNumber;
    private Long hotelId;
    private String hotelName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guests;
    private BigDecimal totalPrice;
    private String status;

    public BookingResponseDTO() {}

    public BookingResponseDTO(Long id, Long userId, Long roomId, String roomNumber, Long hotelId, String hotelName, LocalDate checkInDate, LocalDate checkOutDate, Integer guests, BigDecimal totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.guests = guests;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public static BookingResponseDTOBuilder builder() {
        return new BookingResponseDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public Integer getGuests() { return guests; }
    public void setGuests(Integer guests) { this.guests = guests; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static class BookingResponseDTOBuilder {
        private Long id;
        private Long userId;
        private Long roomId;
        private String roomNumber;
        private Long hotelId;
        private String hotelName;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private Integer guests;
        private BigDecimal totalPrice;
        private String status;

        public BookingResponseDTOBuilder id(Long id) { this.id = id; return this; }
        public BookingResponseDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public BookingResponseDTOBuilder roomId(Long roomId) { this.roomId = roomId; return this; }
        public BookingResponseDTOBuilder roomNumber(String roomNumber) { this.roomNumber = roomNumber; return this; }
        public BookingResponseDTOBuilder hotelId(Long hotelId) { this.hotelId = hotelId; return this; }
        public BookingResponseDTOBuilder hotelName(String hotelName) { this.hotelName = hotelName; return this; }
        public BookingResponseDTOBuilder checkInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; return this; }
        public BookingResponseDTOBuilder checkOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; return this; }
        public BookingResponseDTOBuilder guests(Integer guests) { this.guests = guests; return this; }
        public BookingResponseDTOBuilder totalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; return this; }
        public BookingResponseDTOBuilder status(String status) { this.status = status; return this; }

        public BookingResponseDTO build() {
            return new BookingResponseDTO(id, userId, roomId, roomNumber, hotelId, hotelName, checkInDate, checkOutDate, guests, totalPrice, status);
        }
    }
}
