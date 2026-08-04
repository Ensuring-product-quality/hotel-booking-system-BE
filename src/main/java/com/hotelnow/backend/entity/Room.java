package com.hotelnow.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "rooms",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_room_hotel_number",
                columnNames = {"hotel_id", "room_number"})
)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer capacity = 2;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Room() {}

    public Room(Long id, Hotel hotel, String roomNumber, RoomType type, BigDecimal price, Integer capacity, String description, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.hotel = hotel;
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.capacity = capacity != null ? capacity : 2;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RoomBuilder builder() {
        return new RoomBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class RoomBuilder {
        private Long id;
        private Hotel hotel;
        private String roomNumber;
        private RoomType type;
        private BigDecimal price;
        private Integer capacity = 2;
        private String description;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public RoomBuilder id(Long id) { this.id = id; return this; }
        public RoomBuilder hotel(Hotel hotel) { this.hotel = hotel; return this; }
        public RoomBuilder roomNumber(String roomNumber) { this.roomNumber = roomNumber; return this; }
        public RoomBuilder type(RoomType type) { this.type = type; return this; }
        public RoomBuilder price(BigDecimal price) { this.price = price; return this; }
        public RoomBuilder capacity(Integer capacity) { this.capacity = capacity; return this; }
        public RoomBuilder description(String description) { this.description = description; return this; }
        public RoomBuilder status(String status) { this.status = status; return this; }
        public RoomBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public RoomBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Room build() {
            return new Room(id, hotel, roomNumber, type, price, capacity, description, status, createdAt, updatedAt);
        }
    }
}
