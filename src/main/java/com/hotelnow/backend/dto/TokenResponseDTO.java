package com.hotelnow.backend.dto;

public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private UserResponseDTO user;

    public TokenResponseDTO() {}

    public TokenResponseDTO(String accessToken, String refreshToken, UserResponseDTO user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public UserResponseDTO getUser() { return user; }
    public void setUser(UserResponseDTO user) { this.user = user; }
}
