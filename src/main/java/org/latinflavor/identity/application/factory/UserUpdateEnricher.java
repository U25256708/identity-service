package org.latinflavor.identity.application.factory;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.command.UpdateUserCommand;
import org.latinflavor.identity.domain.model.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUpdateEnricher {

    public void enrichWithOptionalField(User user, UpdateUserCommand command) {
        if (command == null) return;

        if (command.firstName() != null) user.setFirstName(command.firstName());
        if (command.paternalLastName() != null) user.setPaternalLastName(command.paternalLastName());
        if (command.maternalLastName() != null) user.setMaternalLastName(command.maternalLastName());
        if (command.phoneNumber() != null) user.setPhoneNumber(command.phoneNumber());
    }
}
