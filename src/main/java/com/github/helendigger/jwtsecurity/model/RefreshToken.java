package com.github.helendigger.jwtsecurity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private String id;
    private Long userId;
    private String value;
}
