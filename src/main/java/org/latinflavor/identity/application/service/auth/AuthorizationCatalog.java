package org.latinflavor.identity.application.service.auth;

import org.latinflavor.identity.domain.model.Permission;
import org.latinflavor.identity.domain.model.PermissionCode;
import org.latinflavor.identity.domain.model.Role;
import org.latinflavor.identity.domain.model.RoleName;
import org.latinflavor.identity.domain.errors.UserErrors;
import org.latinflavor.identity.shared.exception.ApplicationException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class AuthorizationCatalog {

    private final Map<RoleName, Role> roles = new EnumMap<>(RoleName.class);
    private final Map<PermissionCode, Permission> permissions = new EnumMap<>(PermissionCode.class);

    public void register(Role role) {
        roles.put(role.getName(), role);
    }

    public void register(Permission permission) {
        permissions.put(permission.getCode(), permission);
    }

    public Role requireActiveRole(RoleName name) {
        Role role = roles.get(name);
        if (role == null || !role.isActive()) {
            throw new ApplicationException(UserErrors.ROLE_NOT_FOUND, name);
        }
        return role;
    }

    public Permission requireActivePermission(PermissionCode code) {
        Permission permission = permissions.get(code);
        if (permission == null || !permission.isActive()) {
            throw new ApplicationException(UserErrors.PERMISSION_NOT_FOUND, code);
        }
        return permission;
    }
}
