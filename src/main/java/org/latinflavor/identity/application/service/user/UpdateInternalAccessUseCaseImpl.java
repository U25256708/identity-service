package org.latinflavor.identity.application.service.user;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.command.UpdateInternalAccessCommand;
import org.latinflavor.identity.application.port.in.user.UpdateInternalAccessUseCase;
import org.latinflavor.identity.application.port.out.user.UserPersistPort;
import org.latinflavor.identity.application.port.out.user.UserSearchPort;
import org.latinflavor.identity.application.service.auth.AuthorizationCatalog;
import org.latinflavor.identity.domain.errors.UserErrors;
import org.latinflavor.identity.domain.model.Permission;
import org.latinflavor.identity.domain.model.Role;
import org.latinflavor.identity.domain.model.User;
import org.latinflavor.identity.domain.model.UserType;
import org.latinflavor.identity.shared.exception.ApplicationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateInternalAccessUseCaseImpl implements UpdateInternalAccessUseCase {

    private final UserPersistPort userPersistPort;
    private final UserSearchPort userSearchPort;
    private final AuthorizationCatalog authorizationCatalog;

    @Override
    @Transactional
    public void update(String id, UpdateInternalAccessCommand command) {
        User user = userSearchPort.findByIdWithDetails(id)
                .orElseThrow(() -> new ApplicationException(UserErrors.USER_NOT_FOUND, id));

        if (user.getUserType() != UserType.INTERNAL) {
            throw new ApplicationException(UserErrors.USER_NOT_INTERNAL, id);
        }

        Role role = authorizationCatalog.requireActiveRole(command.role());
        Set<Permission> permissions = command.permissions().stream()
                .map(authorizationCatalog::requireActivePermission)
                .collect(Collectors.toSet());

        userPersistPort.persist(user.replaceRole(role).applyPermissions(permissions));
    }
}
