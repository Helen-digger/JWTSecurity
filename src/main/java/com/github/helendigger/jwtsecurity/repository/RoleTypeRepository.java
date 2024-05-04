package com.github.helendigger.jwtsecurity.repository;

import com.github.helendigger.jwtsecurity.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleTypeRepository extends JpaRepository<RoleType, Long> {
    Optional<RoleType> findRoleTypeByName(String name);

}
