package org.latinflavor.identity.adapter.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 6) String password,
        @NotBlank @Email String email
) {}
