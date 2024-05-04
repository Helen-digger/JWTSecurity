package com.github.helendigger.jwtsecurity.security;

import com.github.helendigger.jwtsecurity.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityAuthConverter implements AuthenticationConverter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final TokenService tokenService;
    @Override
    public Authentication convert(HttpServletRequest request) {
        return extractBearerToken(request).flatMap(tokenService::toAuthentication)
                .orElseGet(tokenService::emptyAuthentication);
    }

    private Optional<String> extractBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(token -> token.startsWith(BEARER_PREFIX))
                .map(token -> token.substring(BEARER_PREFIX.length()));
    }
}
