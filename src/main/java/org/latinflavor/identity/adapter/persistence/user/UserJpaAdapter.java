package org.latinflavor.identity.adapter.persistence.user;

import org.latinflavor.identity.adapter.persistence.user.port.UserPersistPort;
import org.latinflavor.identity.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements UserPersistPort {

    private final UserRepository repository;

    @Override
    public User persist(User entity) {
        return repository.save(entity);
    }


}
