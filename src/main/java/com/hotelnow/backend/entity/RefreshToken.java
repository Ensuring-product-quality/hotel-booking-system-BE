package com.hotelnow.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token_token_id", columnList = "token_id", unique = true),
                @Index(name = "idx_refresh_token_user_id", columnList = "user_id")
        }
)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id", nullable = false, unique = true, length = 36)
    private String tokenId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    private LocalDateTime revokedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public RefreshToken() {}

    public RefreshToken(Long id, String tokenId, User user, LocalDateTime expiresAt, boolean revoked, LocalDateTime revokedAt, LocalDateTime createdAt) {
        this.id = id;
        this.tokenId = tokenId;
        this.user = user;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.revokedAt = revokedAt;
        this.createdAt = createdAt;
    }

    public static RefreshTokenBuilder builder() {
        return new RefreshTokenBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTokenId() { return tokenId; }
    public void setTokenId(String tokenId) { this.tokenId = tokenId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class RefreshTokenBuilder {
        private Long id;
        private String tokenId;
        private User user;
        private LocalDateTime expiresAt;
        private boolean revoked = false;
        private LocalDateTime revokedAt;
        private LocalDateTime createdAt;

        public RefreshTokenBuilder id(Long id) { this.id = id; return this; }
        public RefreshTokenBuilder tokenId(String tokenId) { this.tokenId = tokenId; return this; }
        public RefreshTokenBuilder user(User user) { this.user = user; return this; }
        public RefreshTokenBuilder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public RefreshTokenBuilder revoked(boolean revoked) { this.revoked = revoked; return this; }
        public RefreshTokenBuilder revokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; return this; }
        public RefreshTokenBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public RefreshToken build() {
            return new RefreshToken(id, tokenId, user, expiresAt, revoked, revokedAt, createdAt);
        }
    }
}
