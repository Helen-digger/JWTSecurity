package com.github.helendigger.jwtsecurity.service;

import com.github.helendigger.jwtsecurity.configuration.JwtConfiguration;
import com.github.helendigger.jwtsecurity.configuration.PostgresConfiguration;
import com.github.helendigger.jwtsecurity.model.dto.CreateUserRequest;
import com.github.helendigger.jwtsecurity.model.dto.RoleTypeField;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;

@SpringBootTest(classes = {PostgresConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest implements JwtConfiguration {
    @Autowired
    UserService service;

    @Test
    public void createUserTest() {
        var request = new CreateUserRequest();
        request.setUsername("user");
        request.setPassword("password");
        request.setRoles(Set.of(RoleTypeField.USER, RoleTypeField.ADMIN));
        request.setEmail("example@example.com");
        var id = service.createUser(request);
        Assertions.assertNotNull(id);
        Assertions.assertNotNull(id.getId());
    }

    @Test
    public void createUserInvalidDataTest() {
        var request = new CreateUserRequest();
        request.setUsername("user");
        request.setPassword("password");
        request.setRoles(Set.of(RoleTypeField.USER, RoleTypeField.ADMIN));
        request.setEmail("not email");
        Assertions.assertThrows(ConstraintViolationException.class, () -> service.createUser(request));
    }

    @Test
    public void findUserByIdTest() {
        var request = new CreateUserRequest();
        request.setUsername("user");
        request.setPassword("password");
        request.setRoles(Set.of(RoleTypeField.USER, RoleTypeField.ADMIN));
        request.setEmail("example@example.com");
        var id = service.createUser(request);
        Assertions.assertNotNull(id);
        Assertions.assertNotNull(id.getId());

        var userOpt = service.findById(id.getId());
        Assertions.assertTrue(userOpt.isPresent());
    }

    @Test
    public void findUserByIdEmpty() {
        var userOpts = service.findById(1L);
        Assertions.assertTrue(userOpts.isEmpty());
    }

    @Test
    public void findByUsernameTest() {
        var request = new CreateUserRequest();
        request.setUsername("user");
        request.setPassword("password");
        request.setRoles(Set.of(RoleTypeField.USER, RoleTypeField.ADMIN));
        request.setEmail("example@example.com");
        var id = service.createUser(request);
        Assertions.assertNotNull(id);
        Assertions.assertNotNull(id.getId());

        var userOpt = service.findByUsername(request.getUsername());
        Assertions.assertTrue(userOpt.isPresent());
    }

    @Test
    public void findByUsernameTestEmpty() {
        var userOpt = service.findByUsername("user");
        Assertions.assertTrue(userOpt.isEmpty());
    }
}
