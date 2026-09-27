package org.latinflavor.identity.application.service.user;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.port.in.user.GetUserUseCase;
import org.latinflavor.identity.application.port.out.user.UserSearchPort;
import org.latinflavor.identity.domain.errors.UserErrors;
import org.latinflavor.identity.domain.model.User;
import org.latinflavor.identity.shared.exception.ApplicationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserUseCaseImpl implements GetUserUseCase {

    private final UserSearchPort userSearchPort;

    @Override
    @Transactional(readOnly = true)
    public User get(String id) {
        return userSearchPort.findByIdWithDetails(id)
                .orElseThrow(() -> new ApplicationException(UserErrors.USER_NOT_FOUND, id));
    }
}
