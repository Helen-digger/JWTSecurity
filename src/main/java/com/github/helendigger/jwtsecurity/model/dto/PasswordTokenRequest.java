package com.github.helendigger.jwtsecurity.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordTokenRequest {
    @NotNull
    private String username;
    @NotNull
    private String password;
}
