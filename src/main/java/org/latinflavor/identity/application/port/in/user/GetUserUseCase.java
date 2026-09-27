package org.latinflavor.identity.application.port.in.user;

import org.latinflavor.identity.domain.model.User;

public interface GetUserUseCase {

    User get(String id);
}
