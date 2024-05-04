package com.github.helendigger.jwtsecurity.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@TestConfiguration
public interface JwtConfiguration {
    @DynamicPropertySource
    static void jwtConfig(DynamicPropertyRegistry registry) {
        registry.add("jwt.secret", () -> "test");
        registry.add("jwt.tokenExpiration", () -> "5s");
        registry.add("jwt.refreshTokenExpiration", () -> "5s");
    }
}
