package org.latinflavor.identity.application.port.in.user;

import org.latinflavor.identity.application.command.CreateUserCommand;
import org.latinflavor.identity.domain.model.User;

public interface CreateUserUseCase {

    User create(CreateUserCommand command);
}
