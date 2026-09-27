package org.latinflavor.identity.application.service.user;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.factory.UserFactory;
import org.latinflavor.identity.application.port.in.user.CreateUserUseCase;
import org.latinflavor.identity.application.command.CreateUserCommand;
import org.latinflavor.identity.application.port.out.user.UserPersistPort;
import org.latinflavor.identity.application.port.out.user.UserSearchPort;
import org.latinflavor.identity.domain.model.User;
import org.latinflavor.identity.shared.exception.ApplicationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.latinflavor.identity.domain.errors.UserErrors.*;

@Service
@RequiredArgsConstructor
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserPersistPort userPersistPort;
    private final UserSearchPort userSearchPort;
    private final UserFactory userFactory;

    @Override
    @Transactional
    public User create(CreateUserCommand command) {
        checkEmail(command);
        ensureUniqueFields(command);
        return userPersistPort.persist(userFactory.createInternalUser(command));
    }

    private void checkEmail(CreateUserCommand command) {
        userSearchPort.findByEmail(command.email())
                .ifPresent(existing -> {
                    throw new ApplicationException(USER_EMAIL_ALREADY_EXISTS, command.email());
                });
    }

    void ensureUniqueFields(CreateUserCommand data) {
        if (hasText(data.username())) {
            userSearchPort.findByUsername(data.username())
                    .ifPresent(existing -> {
                        throw new ApplicationException(USER_USERNAME_ALREADY_EXISTS, data.username());
                    });
        }
        //TODO: Remover esto en documentacion
        if (hasText(data.dni())) {
            userSearchPort.findByDni(data.dni())
                    .ifPresent(existing -> {
                        throw new ApplicationException(USER_DNI_ALREADY_EXISTS, data.dni());
                    });
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
