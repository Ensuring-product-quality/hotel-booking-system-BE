package com.hotelnow.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @NotBlank(message = "Email không được để trống")
    @Email
    private String email;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status; // "active", "inactive"

    private String role; // "ADMIN", "STAFF", "CUSTOMER"
}
