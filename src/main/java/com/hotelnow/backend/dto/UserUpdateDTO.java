package com.hotelnow.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserUpdateDTO {
    @NotBlank(message = "Email không được để trống")
    @Email
    private String email;

    private String fullName;

    @Pattern(regexp = "^$|^(0[3|5|7|8|9])[0-9]{8}$", message = "Số điện thoại không hợp lệ (VD: 0912345678)")
    private String phone;

    private String status;

    private String role;

    public UserUpdateDTO() {}

    public UserUpdateDTO(String email, String fullName, String phone, String status, String role) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.status = status;
        this.role = role;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
