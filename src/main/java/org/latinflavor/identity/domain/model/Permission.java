package org.latinflavor.identity.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.latinflavor.identity.shared.persistence.AbstractAuditable;
import java.util.UUID;

@Entity
@Table(name = "permissions", schema = "identity_schema")
@Getter
@Setter
@NoArgsConstructor
public class Permission extends AbstractAuditable<UUID> {

    @Enumerated(EnumType.STRING)
    private PermissionCode code;

    private Boolean isActive;

    private Permission(PermissionCode code) {
        this.code = code;
        this.isActive = true;
    }

    public static Permission of(PermissionCode code) {
        return new Permission(code);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }
}
