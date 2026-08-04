package com.hotelnow.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingDetailDTO {
    private Long id;
    private UserResponseDTO user;
    private RoomResponseDTO room;
    private Long hotelId;
    private String hotelName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guests;
    private BigDecimal totalPrice;
    private String status;
    private String paymentStatus;

    public BookingDetailDTO() {}

    public BookingDetailDTO(Long id, UserResponseDTO user, RoomResponseDTO room, Long hotelId, String hotelName, LocalDate checkInDate, LocalDate checkOutDate, Integer guests, BigDecimal totalPrice, String status, String paymentStatus) {
        this.id = id;
        this.user = user;
        this.room = room;
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.guests = guests;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public static BookingDetailDTOBuilder builder() {
        return new BookingDetailDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserResponseDTO getUser() { return user; }
    public void setUser(UserResponseDTO user) { this.user = user; }

    public RoomResponseDTO getRoom() { return room; }
    public void setRoom(RoomResponseDTO room) { this.room = room; }

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

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public static class BookingDetailDTOBuilder {
        private Long id;
        private UserResponseDTO user;
        private RoomResponseDTO room;
        private Long hotelId;
        private String hotelName;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private Integer guests;
        private BigDecimal totalPrice;
        private String status;
        private String paymentStatus;

        public BookingDetailDTOBuilder id(Long id) { this.id = id; return this; }
        public BookingDetailDTOBuilder user(UserResponseDTO user) { this.user = user; return this; }
        public BookingDetailDTOBuilder room(RoomResponseDTO room) { this.room = room; return this; }
        public BookingDetailDTOBuilder hotelId(Long hotelId) { this.hotelId = hotelId; return this; }
        public BookingDetailDTOBuilder hotelName(String hotelName) { this.hotelName = hotelName; return this; }
        public BookingDetailDTOBuilder checkInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; return this; }
        public BookingDetailDTOBuilder checkOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; return this; }
        public BookingDetailDTOBuilder guests(Integer guests) { this.guests = guests; return this; }
        public BookingDetailDTOBuilder totalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; return this; }
        public BookingDetailDTOBuilder status(String status) { this.status = status; return this; }
        public BookingDetailDTOBuilder paymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; return this; }

        public BookingDetailDTO build() {
            return new BookingDetailDTO(id, user, room, hotelId, hotelName, checkInDate, checkOutDate, guests, totalPrice, status, paymentStatus);
        }
    }
}
