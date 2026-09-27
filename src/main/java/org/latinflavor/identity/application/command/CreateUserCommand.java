package org.latinflavor.identity.application.command;

import org.latinflavor.identity.domain.model.PermissionCode;
import org.latinflavor.identity.domain.model.RoleName;

import java.util.Set;

public record CreateUserCommand(
        RoleName role,
        String username,
        String password,
        String firstName,
        String paternalLastName,
        String maternalLastName,
        String email,
        String phoneNumber,
        String dni,
        Set<PermissionCode> permissions
) {
}
