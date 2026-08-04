package com.hotelnow.backend.dto;

import java.util.List;

public class UserDetailDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private String status;
    private String avatarUrl;
    private List<BookingResponseDTO> bookings;

    public UserDetailDTO() {}

    public UserDetailDTO(Long id, String username, String email, String fullName, String phone, String role, String status, String avatarUrl, List<BookingResponseDTO> bookings) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.avatarUrl = avatarUrl;
        this.bookings = bookings;
    }

    public static UserDetailDTOBuilder builder() {
        return new UserDetailDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public List<BookingResponseDTO> getBookings() { return bookings; }
    public void setBookings(List<BookingResponseDTO> bookings) { this.bookings = bookings; }

    public static class UserDetailDTOBuilder {
        private Long id;
        private String username;
        private String email;
        private String fullName;
        private String phone;
        private String role;
        private String status;
        private String avatarUrl;
        private List<BookingResponseDTO> bookings;

        public UserDetailDTOBuilder id(Long id) { this.id = id; return this; }
        public UserDetailDTOBuilder username(String username) { this.username = username; return this; }
        public UserDetailDTOBuilder email(String email) { this.email = email; return this; }
        public UserDetailDTOBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserDetailDTOBuilder phone(String phone) { this.phone = phone; return this; }
        public UserDetailDTOBuilder role(String role) { this.role = role; return this; }
        public UserDetailDTOBuilder status(String status) { this.status = status; return this; }
        public UserDetailDTOBuilder avatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; return this; }
        public UserDetailDTOBuilder bookings(List<BookingResponseDTO> bookings) { this.bookings = bookings; return this; }

        public UserDetailDTO build() {
            return new UserDetailDTO(id, username, email, fullName, phone, role, status, avatarUrl, bookings);
        }
    }
}
