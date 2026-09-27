package org.latinflavor.identity.adapter.persistence.role;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.port.out.role.RolePersistPort;
import org.latinflavor.identity.application.port.out.role.RoleSearchPort;
import org.latinflavor.identity.domain.model.Role;
import org.latinflavor.identity.domain.model.RoleName;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RolePersistPort, RoleSearchPort {

    private final RoleRepository repository;

    @Override
    public Role persist(Role role) {
        return repository.save(role);
    }

    @Override
    public Optional<Role> findByName(RoleName name) {
        return repository.findByName(name);
    }

    @Override
    public Optional<Role> findActiveByName(RoleName name) {
        return repository.findActiveByName(name);
    }
}
