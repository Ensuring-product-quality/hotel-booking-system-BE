package com.hotelnow.backend.service;

import com.hotelnow.backend.dto.*;
import com.hotelnow.backend.entity.RefreshToken;
import com.hotelnow.backend.entity.Role;
import com.hotelnow.backend.entity.User;
import com.hotelnow.backend.exception.BadRequestException;
import com.hotelnow.backend.repository.RefreshTokenRepository;
import com.hotelnow.backend.repository.UserRepository;
import com.hotelnow.backend.security.JwtTokenProvider;
import com.hotelnow.backend.security.TokenType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public UserResponseDTO register(UserRegisterDTO registerDTO) {
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new BadRequestException("Tên đăng nhập này đã được sử dụng");
        }
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new BadRequestException("Email này đã được đăng ký tài khoản");
        }

        // Standard user role is CUSTOMER, starting with active status for development testing
        User user = User.builder()
                .username(registerDTO.getUsername())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .email(registerDTO.getEmail())
                .role(Role.CUSTOMER)
                .status("active")
                .build();

        return mapToUserResponse(userRepository.save(user));
    }

    public void verifyEmail(String token) {
        throw new BadRequestException("Email verification is not required for this environment");
    }

    @Transactional
    public TokenResponseDTO login(LoginRequestDTO loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .or(() -> userRepository.findByEmail(loginRequest.getUsername()))
                .orElseThrow(() -> new BadRequestException("Tên đăng nhập hoặc mật khẩu không chính xác"));

        if (!"active".equalsIgnoreCase(user.getStatus())) {
            throw new BadRequestException("Account is inactive");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        saveRefreshToken(refreshToken, user);

        return new TokenResponseDTO(accessToken, refreshToken, mapToUserResponse(user));
    }

    @Transactional
    public TokenResponseDTO refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)
                || !tokenProvider.isTokenType(refreshToken, TokenType.REFRESH)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String username = tokenProvider.getUsernameFromJwt(refreshToken);
        String tokenId = tokenProvider.getTokenId(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByTokenIdAndRevokedFalse(tokenId)
                .orElseThrow(() -> new BadRequestException("Refresh token has been revoked"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy người dùng"));

        if (!storedToken.getUser().getId().equals(user.getId())
                || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invalid or expired refresh token");
        }
        if (!"active".equalsIgnoreCase(user.getStatus())) {
            throw new BadRequestException("Account is inactive");
        }

        revoke(storedToken);
        String newAccessToken = tokenProvider.generateAccessToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);
        saveRefreshToken(newRefreshToken, user);

        return new TokenResponseDTO(newAccessToken, newRefreshToken, mapToUserResponse(user));
    }

    @Transactional
    public void logout(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)
                || !tokenProvider.isTokenType(refreshToken, TokenType.REFRESH)) {
            throw new BadRequestException("Invalid refresh token");
        }
        refreshTokenRepository.findByTokenIdAndRevokedFalse(tokenProvider.getTokenId(refreshToken))
                .ifPresent(this::revoke);
    }

    public void forgotPassword(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email not registered");
        }
        throw new BadRequestException("Password reset email is not configured");
    }

    private void saveRefreshToken(String token, User user) {
        RefreshToken storedToken = RefreshToken.builder()
                .tokenId(tokenProvider.getTokenId(token))
                .user(user)
                .expiresAt(LocalDateTime.ofInstant(
                        tokenProvider.getExpiration(token).toInstant(),
                        ZoneId.systemDefault()))
                .build();
        refreshTokenRepository.save(storedToken);
    }

    private void revoke(RefreshToken token) {
        token.setRevoked(true);
        token.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(token);
    }

    private UserResponseDTO mapToUserResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
