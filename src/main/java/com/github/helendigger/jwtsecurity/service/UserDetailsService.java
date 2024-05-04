package com.github.helendigger.jwtsecurity.service;

import com.github.helendigger.jwtsecurity.model.AppUserDetails;
import com.github.helendigger.jwtsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class UserDetailsService {
    private final UserRepository userRepository;

    public Optional<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username).map(AppUserDetails::new);
    }
}
