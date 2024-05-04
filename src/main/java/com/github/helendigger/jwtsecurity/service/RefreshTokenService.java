package com.github.helendigger.jwtsecurity.service;

import com.github.helendigger.jwtsecurity.model.RefreshToken;
import com.github.helendigger.jwtsecurity.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken save(Long userId) {
        String refreshTokenValue = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken(id, userId, refreshTokenValue);

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> getByValue(String refreshToken) {
        return refreshTokenRepository.getByValue(refreshToken);
    }
}
