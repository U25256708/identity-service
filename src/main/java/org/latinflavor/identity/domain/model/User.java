package org.latinflavor.identity.domain.model;

import org.latinflavor.identity.shared.model.AbstractAuditable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "identity_schema")
@Getter
@Setter
@NoArgsConstructor
public class User extends AbstractAuditable<UUID> {

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String username;
    private String passwordHash;
    private String firstName;
    private String paternalLastName;
    private String maternalLastName;
    private String email;
    private String phoneNumber;
    private String dni;
    private String employeeCode;
    private Boolean active;
    private OffsetDateTime lastLoginAt;
    private Integer tokenVersion;

}