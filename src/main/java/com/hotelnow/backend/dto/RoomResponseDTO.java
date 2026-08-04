package com.hotelnow.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class RoomResponseDTO {
    private Long id;
    private Long hotelId;
    private String roomNumber;
    private String type;
    private BigDecimal price;
    private Integer capacity;
    private String description;
    private String status;
    private List<String> images;

    public RoomResponseDTO() {}

    public RoomResponseDTO(Long id, Long hotelId, String roomNumber, String type, BigDecimal price, Integer capacity, String description, String status, List<String> images) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.capacity = capacity;
        this.description = description;
        this.status = status;
        this.images = images;
    }

    public static RoomResponseDTOBuilder builder() {
        return new RoomResponseDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public static class RoomResponseDTOBuilder {
        private Long id;
        private Long hotelId;
        private String roomNumber;
        private String type;
        private BigDecimal price;
        private Integer capacity;
        private String description;
        private String status;
        private List<String> images;

        public RoomResponseDTOBuilder id(Long id) { this.id = id; return this; }
        public RoomResponseDTOBuilder hotelId(Long hotelId) { this.hotelId = hotelId; return this; }
        public RoomResponseDTOBuilder roomNumber(String roomNumber) { this.roomNumber = roomNumber; return this; }
        public RoomResponseDTOBuilder type(String type) { this.type = type; return this; }
        public RoomResponseDTOBuilder price(BigDecimal price) { this.price = price; return this; }
        public RoomResponseDTOBuilder capacity(Integer capacity) { this.capacity = capacity; return this; }
        public RoomResponseDTOBuilder description(String description) { this.description = description; return this; }
        public RoomResponseDTOBuilder status(String status) { this.status = status; return this; }
        public RoomResponseDTOBuilder images(List<String> images) { this.images = images; return this; }

        public RoomResponseDTO build() {
            return new RoomResponseDTO(id, hotelId, roomNumber, type, price, capacity, description, status, images);
        }
    }
}
