package org.latinflavor.identity.application.port.in.user;

import org.latinflavor.identity.domain.model.User;

import java.util.List;

public interface GetUsersUseCase {

    List<User> searchInternalUsers();
}
