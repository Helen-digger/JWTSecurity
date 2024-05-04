package com.github.helendigger.jwtsecurity.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    @NotNull(message = "Username is mandatory")
    @Size(min = 3, max = 255)
    private String username;
    @NotNull(message = "User password is mandatory")
    @Size(min = 5, message = "Minimal password length is 5 symbols")
    private String password;
    @NotNull(message = "User email is mandatory")
    @Email(message = "Invalid email format")
    private String email;
    private Set<RoleTypeField> roles = new HashSet<>();
}