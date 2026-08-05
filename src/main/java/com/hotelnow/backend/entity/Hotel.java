package com.hotelnow.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Integer stars; // 1-5

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String status; // "active", "inactive"

    @Column(nullable = false)
    private Double averageRating = 0.0;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Hotel() {}

    public Hotel(Long id, String name, String address, String city, Integer stars, String description, String status, Double averageRating, String imageUrl, User manager, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.stars = stars;
        this.description = description;
        this.status = status;
        this.averageRating = averageRating != null ? averageRating : 0.0;
        this.imageUrl = imageUrl;
        this.manager = manager;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static HotelBuilder builder() {
        return new HotelBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public User getManager() { return manager; }
    public void setManager(User manager) { this.manager = manager; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class HotelBuilder {
        private Long id;
        private String name;
        private String address;
        private String city;
        private Integer stars;
        private String description;
        private String status;
        private Double averageRating = 0.0;
        private String imageUrl;
        private User manager;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public HotelBuilder id(Long id) { this.id = id; return this; }
        public HotelBuilder name(String name) { this.name = name; return this; }
        public HotelBuilder address(String address) { this.address = address; return this; }
        public HotelBuilder city(String city) { this.city = city; return this; }
        public HotelBuilder stars(Integer stars) { this.stars = stars; return this; }
        public HotelBuilder description(String description) { this.description = description; return this; }
        public HotelBuilder status(String status) { this.status = status; return this; }
        public HotelBuilder averageRating(Double averageRating) { this.averageRating = averageRating; return this; }
        public HotelBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public HotelBuilder manager(User manager) { this.manager = manager; return this; }
        public HotelBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public HotelBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Hotel build() {
            return new Hotel(id, name, address, city, stars, description, status, averageRating, imageUrl, manager, createdAt, updatedAt);
        }
    }
}
