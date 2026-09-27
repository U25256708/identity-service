package org.latinflavor.identity.adapter.persistence.permission;

import org.latinflavor.identity.domain.model.Permission;
import org.latinflavor.identity.domain.model.PermissionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByCode(PermissionCode code);
}
