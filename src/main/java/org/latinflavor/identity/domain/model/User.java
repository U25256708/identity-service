package org.latinflavor.identity.domain.model;

import jakarta.persistence.*;
import org.latinflavor.identity.shared.persistence.AbstractAuditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.latinflavor.identity.domain.model.UserType.CUSTOMER;
import static org.latinflavor.identity.domain.model.UserType.INTERNAL;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", schema = "identity_schema",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_permissions", schema = "identity_schema",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    private User(String email) {
        this.email = requireNonNull(email, "email must be not null");
        this.active = true;
        this.userType = CUSTOMER;
        this.tokenVersion = 1;
    }

    public User(String username, String passwordHashed, String firstName, String paternalLastName,
                String maternalLastName, String email, String phoneNumber, String dni) {

        this.username = requireNonNull(username, "username must be not null");
        this.email = requireNonNull(email, "email must be not null");
        this.passwordHash = requireNonNull(passwordHashed, "passwordHashed must be not null");
        this.firstName = requireNonNull(firstName, "firstName must be not null");
        this.paternalLastName = requireNonNull(paternalLastName, "paternalLastName must be not null");
        this.maternalLastName = requireNonNull(maternalLastName, "maternalLastName must be not null");
        this.phoneNumber = requireNonNull(phoneNumber, "maternalLastName must be not null");
        this.dni = requireNonNull(dni, "dni must be not null");
        this.employeeCode = this.firstName.charAt(0) + dni;
        this.active = true;
        this.userType = INTERNAL;
        this.tokenVersion = 1;
    }

    public static User ofCustomer(String email) {
        return new User(email);
    }

    public static User ofInternal(String username, String passwordHashed, String firstName,
                                  String paternalLastName, String maternalLastName, String email,
                                  String phoneNumber, String dni) {
        return new User(username, passwordHashed, firstName, paternalLastName,
                maternalLastName, email, phoneNumber, dni);
    }

    public User assignRole(Role role) {
        this.roles.add(requireNonNull(role, "role must not be null"));
        return this;
    }

    public User applyPermissions(Set<Permission> permissions) {
        this.permissions.clear();
        if (permissions != null) {
            this.permissions.addAll(permissions);
        }
        return this;
    }

    public User replaceRole(Role role) {
        this.roles.clear();
        return assignRole(role);
    }

    public User update(Consumer<User> updater) {
        requireNonNull(updater, "updater must not be null")
                .accept(this);
        return this;
    }

    public Set<String> getRoleNames() {
        return roles.stream()
                .filter(Role::isActive)
                .map(Role::getName)
                .map(RoleName::name)
                .collect(toUnmodifiableSet());
    }

    public Set<String> getPermissionCodes() {
        return permissions.stream()
                .filter(Permission::isActive)
                .map(Permission::getCode)
                .map(PermissionCode::name)
                .collect(toUnmodifiableSet());
    }

    public User disable() {
        this.active = false;
        this.tokenVersion = this.tokenVersion == null ? 1 : this.tokenVersion + 1;
        return this;
    }

    public User enable() {
        this.active = true;
        return this;
    }

}
