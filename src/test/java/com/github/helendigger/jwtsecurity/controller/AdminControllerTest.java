package com.github.helendigger.jwtsecurity.controller;

import com.github.helendigger.jwtsecurity.configuration.JwtConfiguration;
import com.github.helendigger.jwtsecurity.configuration.PostgresConfiguration;
import com.github.helendigger.jwtsecurity.model.dto.CreateUserRequest;
import com.github.helendigger.jwtsecurity.model.dto.RoleTypeField;
import com.github.helendigger.jwtsecurity.service.TokenService;
import com.github.helendigger.jwtsecurity.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {PostgresConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureWebMvc
@AutoConfigureMockMvc
public class AdminControllerTest implements JwtConfiguration {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserService userService;
    @Autowired
    TokenService tokenService;
    @Test
    public void accessGrantedTest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("user");
        request.setPassword("password");
        request.setEmail("example@example.com");
        request.setRoles(Set.of(RoleTypeField.ADMIN));
        var user = userService.createUser(request);

        var token = tokenService.generateToken(request.getUsername(),
                user.getId().toString(), List.of(RoleTypeField.ADMIN.name()));

        var bearer = String.format("Bearer %s", token);

        Assertions.assertDoesNotThrow(() -> mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(authenticated().withAuthorities(List.of(new SimpleGrantedAuthority(RoleTypeField.ADMIN.name()))))
                .andExpect(content().string("admin"))
                .andExpect(status().isOk()));
    }

    @Test
    public void accessForbidden() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("user");
        request.setPassword("password");
        request.setEmail("example@example.com");
        request.setRoles(Set.of(RoleTypeField.USER));

        var user = userService.createUser(request);
        var token = tokenService.generateToken(request.getUsername(),
                user.getId().toString(), List.of(RoleTypeField.USER.name()));

        var bearer = String.format("Bearer %s", token);

        Assertions.assertDoesNotThrow(() -> mockMvc.perform(MockMvcRequestBuilders.get("/admin")
                        .header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(authenticated().withAuthorities(List.of(new SimpleGrantedAuthority(RoleTypeField.USER.name()))))
                .andExpect(status().isForbidden()));
    }

    @Test
    public void accessUnauthorized() {
        Assertions.assertDoesNotThrow(() -> mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(status().isUnauthorized()));
    }
}
