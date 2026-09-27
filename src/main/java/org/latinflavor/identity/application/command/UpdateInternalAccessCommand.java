package org.latinflavor.identity.application.command;

import org.latinflavor.identity.domain.model.PermissionCode;
import org.latinflavor.identity.domain.model.RoleName;

import java.util.Set;

public record UpdateInternalAccessCommand(
        RoleName role,
        Set<PermissionCode> permissions
) {
}
