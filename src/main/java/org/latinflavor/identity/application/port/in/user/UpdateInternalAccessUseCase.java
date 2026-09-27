package org.latinflavor.identity.application.port.in.user;

import org.latinflavor.identity.application.command.UpdateInternalAccessCommand;

public interface UpdateInternalAccessUseCase {

    void update(String id, UpdateInternalAccessCommand command);
}
