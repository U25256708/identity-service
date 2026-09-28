package org.latinflavor.identity.application.port.out.user;

import org.latinflavor.identity.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface UserSearchPort {

    Optional<User> findByIdWithDetails(String id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByDni(String dni);

    List<User> findAllInternalWithDetails();

    Page<User> searchInternalUsers(Specification<User> userSpecification, Pageable pageable);
}
