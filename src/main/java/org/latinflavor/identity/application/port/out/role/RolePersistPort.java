package org.latinflavor.identity.application.port.out.role;

import org.latinflavor.identity.domain.model.Role;

public interface RolePersistPort {

    Role persist(Role role);
}
