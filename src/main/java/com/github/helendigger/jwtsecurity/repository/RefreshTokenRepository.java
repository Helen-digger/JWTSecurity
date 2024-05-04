package com.github.helendigger.jwtsecurity.repository;

import com.github.helendigger.jwtsecurity.model.RefreshToken;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@Slf4j
public class RefreshTokenRepository {
    private final Cache<String, RefreshToken> idToRefreshToken;
    private final Cache<String, String> valueToId;

    public RefreshTokenRepository(@Value("${jwt.refreshTokenExpiration}")
                                  Duration refreshTokenExpiration) {
        this.idToRefreshToken = CacheBuilder.newBuilder()
                .expireAfterWrite(refreshTokenExpiration)
                .build();
        this.valueToId = CacheBuilder.newBuilder()
                .expireAfterWrite(refreshTokenExpiration)
                .build();
    }

    public RefreshToken save(RefreshToken refreshToken) {
        idToRefreshToken.put(refreshToken.getId(), refreshToken);
        valueToId.put(refreshToken.getValue(), refreshToken.getId());
        return refreshToken;
    }

    public Optional<RefreshToken> getByValue(String refreshToken) {
        return Optional.ofNullable(valueToId.getIfPresent(refreshToken))
                .map(tokenId -> {
                    valueToId.invalidate(refreshToken);
                    return idToRefreshToken.getIfPresent(tokenId);
                });
    }
}
