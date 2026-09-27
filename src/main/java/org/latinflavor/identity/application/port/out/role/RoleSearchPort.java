package org.latinflavor.identity.application.port.out.role;

import org.latinflavor.identity.domain.model.Role;
import org.latinflavor.identity.domain.model.RoleName;

import java.util.Optional;

public interface RoleSearchPort {

    Optional<Role> findByName(RoleName name);

    Optional<Role> findActiveByName(RoleName name);
}
