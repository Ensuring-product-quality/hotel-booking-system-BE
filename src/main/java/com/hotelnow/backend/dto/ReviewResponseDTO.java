package com.hotelnow.backend.dto;

import java.time.LocalDateTime;

public class ReviewResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long hotelId;
    private Long roomId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponseDTO() {}

    public ReviewResponseDTO(Long id, Long userId, String username, Long hotelId, Long roomId, Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.hotelId = hotelId;
        this.roomId = roomId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public static ReviewResponseDTOBuilder builder() {
        return new ReviewResponseDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class ReviewResponseDTOBuilder {
        private Long id;
        private Long userId;
        private String username;
        private Long hotelId;
        private Long roomId;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;

        public ReviewResponseDTOBuilder id(Long id) { this.id = id; return this; }
        public ReviewResponseDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public ReviewResponseDTOBuilder username(String username) { this.username = username; return this; }
        public ReviewResponseDTOBuilder hotelId(Long hotelId) { this.hotelId = hotelId; return this; }
        public ReviewResponseDTOBuilder roomId(Long roomId) { this.roomId = roomId; return this; }
        public ReviewResponseDTOBuilder rating(Integer rating) { this.rating = rating; return this; }
        public ReviewResponseDTOBuilder comment(String comment) { this.comment = comment; return this; }
        public ReviewResponseDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ReviewResponseDTO build() {
            return new ReviewResponseDTO(id, userId, username, hotelId, roomId, rating, comment, createdAt);
        }
    }
}
