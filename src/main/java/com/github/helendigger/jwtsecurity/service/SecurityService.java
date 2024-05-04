package com.github.helendigger.jwtsecurity.service;

import com.github.helendigger.jwtsecurity.model.RoleType;
import com.github.helendigger.jwtsecurity.model.User;
import com.github.helendigger.jwtsecurity.model.dto.RefreshTokenRequest;
import com.github.helendigger.jwtsecurity.model.dto.TokenData;
import com.github.helendigger.jwtsecurity.model.exception.AuthException;
import com.github.helendigger.jwtsecurity.model.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    public TokenData processPasswordToken(String username, String password) {
        var userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User " + username + "was not found");
        }
        var user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Wrong password. Access denied.");
        }
        return createTokenData(user);
    }

    public Optional<TokenData> processRefreshToken(RefreshTokenRequest request) {
        return refreshTokenService.getByValue(request.getRefreshToken())
                .flatMap(refreshToken -> userService.findById(refreshToken.getUserId()))
                .map(this::createTokenData);
    }

    private TokenData createTokenData(User user) {
        String token = tokenService.generateToken(
                user.getUsername(),
                user.getId().toString(),
                user.getRoles().stream().map(RoleType::getName).toList());
        var refreshToken = refreshTokenService.save(user.getId());
        return new TokenData(token, refreshToken.getValue());
    }
}
