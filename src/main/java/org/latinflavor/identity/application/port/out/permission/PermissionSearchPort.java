package org.latinflavor.identity.application.port.out.permission;

import org.latinflavor.identity.domain.model.Permission;
import org.latinflavor.identity.domain.model.PermissionCode;

import java.util.Optional;

public interface PermissionSearchPort {

    Optional<Permission> findByCode(PermissionCode code);
}
