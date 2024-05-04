package com.github.helendigger.jwtsecurity.service;

import com.github.helendigger.jwtsecurity.configuration.JwtConfiguration;
import com.github.helendigger.jwtsecurity.configuration.PostgresConfiguration;
import com.github.helendigger.jwtsecurity.model.AppUserPrincipal;
import io.jsonwebtoken.Jwts;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = {PostgresConfiguration.class})
public class TokenServiceTest implements JwtConfiguration {
    @Autowired
    TokenService service;

    @Value("${jwt.secret}")
    String jwtSecret;

    String roleClaim = "role";
    String idClaim = "id";

    @Test
    public void testGenerateToken() {
        String username = "tester";
        String id = "200";
        List<String> roles = List.of("ADMIN", "USER");
        var generatedToken = service.generateToken(username, id, roles);
        Assertions.assertNotNull(generatedToken);

        var parsed = Assertions.assertDoesNotThrow(() -> Jwts.parser()
                .setSigningKey(jwtSecret).parseClaimsJws(generatedToken).getBody());
        Assertions.assertNotNull(parsed);
        Assertions.assertEquals(username, parsed.getSubject());
        Assertions.assertEquals(id, parsed.get(idClaim));
        Assertions.assertEquals(roles, parsed.get(roleClaim));
    }

    @Test
    public void testToAuthentication() {
        String username = "tester";
        String id = "200";
        List<String> roles = List.of("ADMIN", "USER");

        var generatedToken = service.generateToken(username, id, roles);

        var authOpt = service.toAuthentication(generatedToken);
        Assertions.assertTrue(authOpt.isPresent());
        var auth = authOpt.get();
        Assertions.assertInstanceOf(AppUserPrincipal.class, auth.getPrincipal());
        AppUserPrincipal principal = (AppUserPrincipal) auth.getPrincipal();
        var parsedUserName = principal.getName();
        var rolesParsed = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        Assertions.assertEquals(username, parsedUserName);
        Assertions.assertEquals(roles, rolesParsed);
    }

    @Test
    public void emptyAuthenticationOpt() {
        String username = "tester";
        String id = "200";
        List<String> roles = List.of("ADMIN", "USER");

        var generatedToken = service.generateToken(username, id, roles);
        Awaitility.await().pollDelay(Duration.ofSeconds(1L)).until(() -> service.toAuthentication(generatedToken),
                Optional::isEmpty);
    }
}
