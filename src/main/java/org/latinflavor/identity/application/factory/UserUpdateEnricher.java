package org.latinflavor.identity.application.factory;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.command.UpdateUserCommand;
import org.latinflavor.identity.application.service.auth.AuthorizationCatalog;
import org.latinflavor.identity.domain.model.Permission;
import org.latinflavor.identity.domain.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserUpdateEnricher {

    private final AuthorizationCatalog authorizationCatalog;

    public void enrichWithOptionalField(User user, UpdateUserCommand command) {
        if (command == null) return;

        if (command.role() != null) {
            user.getRoles().clear();
            user.assignRole(authorizationCatalog.requireActiveRole(command.role()));
        }
        if (command.username() != null) user.setUsername(command.username());
        if (command.firstName() != null) user.setFirstName(command.firstName());
        if (command.paternalLastName() != null) user.setPaternalLastName(command.paternalLastName());
        if (command.maternalLastName() != null) user.setMaternalLastName(command.maternalLastName());
        if (command.email() != null) user.setEmail(command.email());
        if (command.phoneNumber() != null) user.setPhoneNumber(command.phoneNumber());
        if (command.dni() != null) user.setDni(command.dni());
        if (command.permissions() != null) user.applyPermissions(resolvePermissions(command));
    }

    private Set<Permission> resolvePermissions(UpdateUserCommand command) {
        return command.permissions().stream()
                .map(authorizationCatalog::requireActivePermission)
                .collect(Collectors.toSet());
    }
}
