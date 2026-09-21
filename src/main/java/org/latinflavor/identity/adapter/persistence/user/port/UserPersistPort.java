package org.latinflavor.identity.adapter.persistence.user.port;

import org.latinflavor.identity.domain.model.User;

public interface UserPersistPort {

    User persist(User entity);
}
