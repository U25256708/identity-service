package org.latinflavor.identity.application.service.auth;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.port.out.permission.PermissionPersistPort;
import org.latinflavor.identity.application.port.out.permission.PermissionSearchPort;
import org.latinflavor.identity.application.port.out.role.RolePersistPort;
import org.latinflavor.identity.application.port.out.role.RoleSearchPort;
import org.latinflavor.identity.domain.model.Permission;
import org.latinflavor.identity.domain.model.PermissionCode;
import org.latinflavor.identity.domain.model.Role;
import org.latinflavor.identity.domain.model.RoleName;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthorizationCatalogInitializer implements ApplicationRunner {

    private final RoleSearchPort roleSearchPort;
    private final RolePersistPort rolePersistPort;
    private final PermissionSearchPort permissionSearchPort;
    private final PermissionPersistPort permissionPersistPort;
    private final AuthorizationCatalog authorizationCatalog;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (RoleName roleName : RoleName.values()) {
            Role role = roleSearchPort.findByName(roleName)
                    .orElseGet(() -> rolePersistPort.persist(Role.of(roleName)));
            authorizationCatalog.register(role);
        }

        for (PermissionCode permissionCode : PermissionCode.values()) {
            Permission permission = permissionSearchPort.findByCode(permissionCode)
                    .orElseGet(() -> permissionPersistPort.persist(Permission.of(permissionCode)));
            authorizationCatalog.register(permission);
        }
    }

}
