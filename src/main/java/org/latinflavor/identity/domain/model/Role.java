package org.latinflavor.identity.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.latinflavor.identity.shared.persistence.AbstractAuditable;

import java.util.UUID;

@Entity
@Table(name = "roles", schema = "identity_schema")
@Getter
@Setter
@NoArgsConstructor
public class Role extends AbstractAuditable<UUID> {

    @Enumerated(EnumType.STRING)
    private RoleName name;

    private String description;
    private Boolean isActive;

    private Role(RoleName name) {
        this.name = name;
        this.description = name.getDescription();
        this.isActive = true;
    }

    public static Role of(RoleName name) {
        return new Role(name);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }
}
