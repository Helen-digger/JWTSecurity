package com.github.helendigger.jwtsecurity.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenData {
    private String token;
    private String refreshToken;
}
