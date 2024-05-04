package com.github.helendigger.jwtsecurity.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Data
public class AppUserPrincipal implements Principal {
    private final String id;
    private final String name;
    private final List<String> roles;
    @Override
    public String getName() {
        return name;
    }
}
