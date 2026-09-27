package org.latinflavor.identity.application.service.user;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.port.in.user.DeleteUserUseCase;
import org.latinflavor.identity.application.port.out.user.UserPersistPort;
import org.latinflavor.identity.application.port.out.user.UserSearchPort;
import org.latinflavor.identity.domain.errors.UserErrors;
import org.latinflavor.identity.domain.model.User;
import org.latinflavor.identity.shared.exception.ApplicationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.latinflavor.identity.domain.errors.UserErrors.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserPersistPort userPersistPort;
    private final UserSearchPort userSearchPort;

    @Override
    @Transactional
    public void delete(String id) {
        User user = userSearchPort.findByIdWithDetails(id)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND, id));
        userPersistPort.persist(user.disable());
    }
}
