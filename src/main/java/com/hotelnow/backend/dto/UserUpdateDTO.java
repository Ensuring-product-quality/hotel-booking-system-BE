package com.hotelnow.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status; // "active", "inactive"
}
