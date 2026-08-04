package com.hotelnow.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyEmailRequestDTO {
    @NotBlank(message = "Token is required")
    private String token;

    public VerifyEmailRequestDTO() {}

    public VerifyEmailRequestDTO(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
