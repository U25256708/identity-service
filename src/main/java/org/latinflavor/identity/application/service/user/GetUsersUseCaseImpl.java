package org.latinflavor.identity.application.service.user;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.port.in.user.GetUsersUseCase;
import org.latinflavor.identity.application.port.out.user.UserSearchPort;
import org.latinflavor.identity.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUsersUseCaseImpl implements GetUsersUseCase {

    private final UserSearchPort userSearchPort;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userSearchPort.findAll();
    }
}
