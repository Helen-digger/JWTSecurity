package com.github.helendigger.jwtsecurity.service;

import com.github.helendigger.jwtsecurity.model.AppUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    private static final String ROLE_CLAIM = "role";

    private static final String ID_CLAIM = "id";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.tokenExpiration}")
    private Duration tokenExpiration;

    public String generateToken(String username, String id, List<String> roles) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date((new Date().getTime() + tokenExpiration.toMillis())))
                .claim(ROLE_CLAIM, roles)
                .claim(ID_CLAIM, id)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Optional<Authentication> toAuthentication(String token) {
        try {
            Claims tokenBody = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
            String subject = tokenBody.getSubject();
            String id = tokenBody.get(ID_CLAIM, String.class);
            List<String> roles = (List<String>) tokenBody.get(ROLE_CLAIM);

            Principal principal = new AppUserPrincipal(id, subject, roles);

            return Optional.of(new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList()));
        } catch (JwtException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    public Authentication emptyAuthentication() {
        var auth = new UsernamePasswordAuthenticationToken(null, null);
        auth.setAuthenticated(false);
        return auth;
    }
}
