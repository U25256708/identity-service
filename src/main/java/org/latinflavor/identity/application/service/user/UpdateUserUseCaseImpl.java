package org.latinflavor.identity.application.service.user;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.command.UpdateUserCommand;
import org.latinflavor.identity.application.factory.UserUpdateEnricher;
import org.latinflavor.identity.application.port.in.user.UpdateUserUseCase;
import org.latinflavor.identity.application.port.out.user.UserPersistPort;
import org.latinflavor.identity.application.port.out.user.UserSearchPort;
import org.latinflavor.identity.domain.errors.UserErrors;
import org.latinflavor.identity.domain.model.User;
import org.latinflavor.identity.shared.exception.ApplicationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserPersistPort userPersistPort;
    private final UserSearchPort userSearchPort;
    private final UserUpdateEnricher userUpdateEnricher;

    @Override
    @Transactional
    public void update(String id, UpdateUserCommand command) {
        User user = userSearchPort.findByIdWithDetails(id)
                .orElseThrow(() -> new ApplicationException(UserErrors.USER_NOT_FOUND, id));
        userPersistPort.persist(user.update(current ->
                userUpdateEnricher.enrichWithOptionalField(current, command)));
    }
}
