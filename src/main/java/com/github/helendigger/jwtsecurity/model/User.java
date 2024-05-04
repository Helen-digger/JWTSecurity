package com.github.helendigger.jwtsecurity.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "username should not be null")
    @Size(min = 3, max = 255, message = "username should be between 3 and 255 symbols length")
    @Column(unique = true)
    private String username;
    @Email(message = "user email is not valid")
    @NotNull(message = "user email should not be null")
    @Column(unique = true)
    private String email;
    @NotNull
    private String password;
    @ManyToMany
    private List<RoleType> roles;
}
