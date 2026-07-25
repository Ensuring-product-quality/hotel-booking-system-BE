package com.hotelnow.backend.repository;

import com.hotelnow.backend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenIdAndRevokedFalse(String tokenId);
}
