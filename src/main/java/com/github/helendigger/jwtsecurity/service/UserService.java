package com.github.helendigger.jwtsecurity.service;

import com.github.helendigger.jwtsecurity.model.RoleType;
import com.github.helendigger.jwtsecurity.model.User;
import com.github.helendigger.jwtsecurity.model.UserId;
import com.github.helendigger.jwtsecurity.model.dto.CreateUserRequest;
import com.github.helendigger.jwtsecurity.model.dto.RoleTypeField;
import com.github.helendigger.jwtsecurity.model.exception.UserCreateException;
import com.github.helendigger.jwtsecurity.repository.RoleTypeRepository;
import com.github.helendigger.jwtsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleTypeRepository roleTypeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserId createUser(CreateUserRequest request) {
        var exists = userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail());
        if (exists) {
            throw new UserCreateException("User with specified name or email already exists");
        }
        var roles = getRoleTypesByName(request.getRoles());
        if (roles.isEmpty()) {
            throw new UserCreateException("Unknown user roles found in request");
        }
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .roles(roles.get()).build();
        return new UserId(userRepository.saveAndFlush(user).getId());
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    private Optional<List<RoleType>> getRoleTypesByName(Set<RoleTypeField> names) {
        var list = new ArrayList<RoleType>();
        for (RoleTypeField roleName : names) {
            var role = roleTypeRepository.findRoleTypeByName(roleName.name());
            if (role.isEmpty()) {
                return Optional.empty();
            }
            list.add(role.get());
        }
        return Optional.of(list);
    }
}
