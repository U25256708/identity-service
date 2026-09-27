package org.latinflavor.identity.application.factory;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.command.CreateUserCommand;
import org.latinflavor.identity.application.service.auth.AuthorizationCatalog;
import org.latinflavor.identity.domain.model.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserFactory {

    private final AuthorizationCatalog authorizationCatalog;
    private final PasswordEncoder passwordEncoder;

    public User createInternalUser(CreateUserCommand userData) {
        if (userData == null) return null;
        return User.ofInternal(userData.username(), passwordEncoder.encode(userData.password()),
                        userData.firstName(), userData.paternalLastName(), userData.maternalLastName(), userData.email(),
                        userData.phoneNumber(), userData.dni())
                .assignRole(resolveRole(userData.role()))
                .applyPermissions(resolvePermissions(userData.permissions()));

    }

    private Role resolveRole(RoleName roleName) {
        return authorizationCatalog.requireActiveRole(roleName);
    }

    private Set<Permission> resolvePermissions(Set<PermissionCode> permissionCodes) {
        if (permissionCodes == null || permissionCodes.isEmpty()) return new HashSet<>();
        return permissionCodes.stream()
                .map(authorizationCatalog::requireActivePermission)
                .collect(java.util.stream.Collectors.toSet());
    }

}
