package com.github.helendigger.jwtsecurity.repository;

import com.github.helendigger.jwtsecurity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsernameOrEmail(String username, String email);
    Optional<User> findByUsername(String username);
}
