package com.github.helendigger.jwtsecurity.configuration;

import com.github.helendigger.jwtsecurity.model.RoleType;
import com.github.helendigger.jwtsecurity.model.dto.RoleTypeField;
import com.github.helendigger.jwtsecurity.repository.RoleTypeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * Populate roles in database
 */
@Configuration
@AllArgsConstructor
public class RoleTypeConfiguration {
    private RoleTypeRepository roleTypeRepository;

    @PostConstruct
    @Transactional
    public void fillRoles() {
        for (var type: RoleTypeField.values()) {
            var roleType = roleTypeRepository.findRoleTypeByName(type.name());
            if (roleType.isEmpty()) {
                RoleType newType = new RoleType();
                newType.setName(type.name());
                roleTypeRepository.saveAndFlush(newType);
            }
        }
    }
}
