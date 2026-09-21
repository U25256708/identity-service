package org.latinflavor.identity.adapter.persistence.user.port;

import org.latinflavor.identity.domain.model.User;

import java.util.Optional;

public interface UserSearchPort {

    Optional<User> searchById(String id);
}
