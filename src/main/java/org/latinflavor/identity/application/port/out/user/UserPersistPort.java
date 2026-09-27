package org.latinflavor.identity.application.port.out.user;

import org.latinflavor.identity.domain.model.User;

public interface UserPersistPort {

    User persist(User entity);

    void delete(User entity);

}
