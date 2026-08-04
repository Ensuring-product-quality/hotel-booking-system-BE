package com.hotelnow.backend.dto;

import java.time.LocalDateTime;

public class NotificationResponseDTO {
    private Long id;
    private Long userId;
    private String message;
    private String status;
    private LocalDateTime createdAt;

    public NotificationResponseDTO() {}

    public NotificationResponseDTO(Long id, Long userId, String message, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static NotificationResponseDTOBuilder builder() {
        return new NotificationResponseDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class NotificationResponseDTOBuilder {
        private Long id;
        private Long userId;
        private String message;
        private String status;
        private LocalDateTime createdAt;

        public NotificationResponseDTOBuilder id(Long id) { this.id = id; return this; }
        public NotificationResponseDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public NotificationResponseDTOBuilder message(String message) { this.message = message; return this; }
        public NotificationResponseDTOBuilder status(String status) { this.status = status; return this; }
        public NotificationResponseDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public NotificationResponseDTO build() {
            return new NotificationResponseDTO(id, userId, message, status, createdAt);
        }
    }
}
