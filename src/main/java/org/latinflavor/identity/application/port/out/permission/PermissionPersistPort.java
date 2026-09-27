package org.latinflavor.identity.application.port.out.permission;

import org.latinflavor.identity.domain.model.Permission;

public interface PermissionPersistPort {

    Permission persist(Permission permission);
}
