package org.latinflavor.identity.adapter.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.latinflavor.identity.domain.model.PermissionCode;
import org.latinflavor.identity.domain.model.RoleName;

import java.util.Set;

public record UpdateUserRequest(
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotNull RoleName role,
        @NotNull String firstName,
        @NotNull String paternalLastName,
        @NotNull String maternalLastName,
        @NotNull String phoneNumber,
        @NotNull String dni,
        Set<PermissionCode> permissions
) {
}
