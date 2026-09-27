package org.latinflavor.identity.adapter.persistence.user;

import org.latinflavor.identity.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {


    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByDni(String dni);

    @Query("""
                SELECT DISTINCT u
                FROM User u
                LEFT JOIN FETCH u.roles
                LEFT JOIN FETCH u.permissions
                WHERE u.id = :id
            """)
    Optional<User> findByIdWithDetails(@Param("id") UUID id);

    @Query("""
                SELECT DISTINCT u
                FROM User u
                LEFT JOIN FETCH u.roles
                LEFT JOIN FETCH u.permissions
                WHERE u.userType = 'INTERNAL'
            """)
    List<User> findAllInternalWithDetails();
}
