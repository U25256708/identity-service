package org.latinflavor.identity.application.port.in.user;

import org.latinflavor.identity.application.command.UpdateUserCommand;
import org.latinflavor.identity.domain.model.User;

public interface UpdateUserUseCase {

    void update(String id, UpdateUserCommand command);
}
