package com.hotelnow.backend.dto;

public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private String status;
    private String avatarUrl;

    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String username, String email, String fullName, String phone, String role, String status, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.avatarUrl = avatarUrl;
    }

    public static UserResponseDTOBuilder builder() {
        return new UserResponseDTOBuilder();
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

    public static class UserResponseDTOBuilder {
        private Long id;
        private String username;
        private String email;
        private String fullName;
        private String phone;
        private String role;
        private String status;
        private String avatarUrl;

        public UserResponseDTOBuilder id(Long id) { this.id = id; return this; }
        public UserResponseDTOBuilder username(String username) { this.username = username; return this; }
        public UserResponseDTOBuilder email(String email) { this.email = email; return this; }
        public UserResponseDTOBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserResponseDTOBuilder phone(String phone) { this.phone = phone; return this; }
        public UserResponseDTOBuilder role(String role) { this.role = role; return this; }
        public UserResponseDTOBuilder status(String status) { this.status = status; return this; }
        public UserResponseDTOBuilder avatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; return this; }

        public UserResponseDTO build() {
            return new UserResponseDTO(id, username, email, fullName, phone, role, status, avatarUrl);
        }
    }
}
