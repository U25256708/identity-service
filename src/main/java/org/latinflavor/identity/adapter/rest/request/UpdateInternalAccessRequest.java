package org.latinflavor.identity.adapter.rest.request;

import jakarta.validation.constraints.NotNull;
import org.latinflavor.identity.domain.model.PermissionCode;
import org.latinflavor.identity.domain.model.RoleName;

import java.util.Set;

public record UpdateInternalAccessRequest(
        @NotNull RoleName role,
        @NotNull Set<PermissionCode> permissions
) {
}
