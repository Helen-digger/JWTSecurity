package com.github.helendigger.jwtsecurity.service;

import com.github.helendigger.jwtsecurity.configuration.JwtConfiguration;
import com.github.helendigger.jwtsecurity.configuration.PostgresConfiguration;
import com.github.helendigger.jwtsecurity.model.User;
import com.github.helendigger.jwtsecurity.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;

@SpringBootTest(classes = {PostgresConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDetailsServiceTest implements JwtConfiguration {
    @Autowired
    UserDetailsService service;
    @Autowired
    UserRepository repository;

    @Test
    public void testFindByName() {
        var user = new User();
        user.setPassword("password");
        user.setUsername("user");
        user.setRoles(Collections.emptyList());
        user.setEmail("example@example.com");
        repository.save(user);
        var found = service.findByUsername("user");
        Assertions.assertTrue(found.isPresent());
    }

    @Test
    public void testEmptyFindByName() {
        var found = service.findByUsername("user");
        Assertions.assertTrue(found.isEmpty());
    }
}
