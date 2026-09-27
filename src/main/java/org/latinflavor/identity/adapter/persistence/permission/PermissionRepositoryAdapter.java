package org.latinflavor.identity.adapter.persistence.permission;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.port.out.permission.PermissionPersistPort;
import org.latinflavor.identity.application.port.out.permission.PermissionSearchPort;
import org.latinflavor.identity.domain.model.Permission;
import org.latinflavor.identity.domain.model.PermissionCode;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionPersistPort, PermissionSearchPort {

    private final PermissionRepository repository;

    @Override
    public Permission persist(Permission permission) {
        return repository.save(permission);
    }

    @Override
    public Optional<Permission> findByCode(PermissionCode code) {
        return repository.findByCode(code);
    }
}
