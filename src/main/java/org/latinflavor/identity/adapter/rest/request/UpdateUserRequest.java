package org.latinflavor.identity.adapter.rest.request;

import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotNull String firstName,
        @NotNull String paternalLastName,
        @NotNull String maternalLastName,
        @NotNull String phoneNumber,
        @NotNull String dni
) {
}
