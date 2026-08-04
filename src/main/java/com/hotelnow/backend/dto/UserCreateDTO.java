package com.hotelnow.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    private String fullName;

    private String phone;

    @NotBlank(message = "Role is required")
    private String role; // "ADMIN", "CUSTOMER", "MANAGER"

    @NotBlank(message = "Trạng thái không được để trống")
    private String status; // "active", "inactive"
}
