package org.latinflavor.identity.adapter.persistence.role;

import org.latinflavor.identity.domain.model.Role;
import org.latinflavor.identity.domain.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName name);

    @Query("select role from Role role where role.name = :name and role.isActive = true")
    Optional<Role> findActiveByName(@Param("name") RoleName name);
}
