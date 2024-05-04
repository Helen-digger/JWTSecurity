package com.github.helendigger.jwtsecurity.security;

import com.github.helendigger.jwtsecurity.model.AppUserPrincipal;
import com.github.helendigger.jwtsecurity.service.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ServiceAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var principal = Optional.ofNullable(authentication).map(Authentication::getPrincipal)
                .filter(AppUserPrincipal.class::isInstance)
                .map(AppUserPrincipal.class::cast)
                .orElseThrow(() -> new BadCredentialsException("Can't get authentication principal"));
        userDetailsService.findByUsername(principal.getName())
                .filter(UserDetails::isEnabled)
                .orElseThrow(() -> new UsernameNotFoundException("Provided username not found"));
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
