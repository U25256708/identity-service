package org.latinflavor.identity.adapter.persistence.user;

import org.latinflavor.identity.application.port.out.user.UserPersistPort;
import org.latinflavor.identity.application.port.out.user.UserSearchPort;
import org.latinflavor.identity.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserPersistPort, UserSearchPort {

    private final UserRepository repository;

    @Override
    public User persist(User entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<User> findByIdWithDetails(String id) {
        return repository.findByIdWithDetails(UUID.fromString(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsernameIgnoreCase(username);
    }

    @Override
    public Optional<User> findByDni(String dni) {
        return repository.findByDni(dni);
    }

    @Override
    public List<User> findAll() {
        return repository.findAllWithDetails();
    }

    @Override
    public void delete(User entity) {
        repository.delete(entity);
    }

}
