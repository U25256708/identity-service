package org.latinflavor.identity.adapter.rest.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import static org.latinflavor.identity.adapter.rest.request.SignInRequest.AuthType.USERNAME_PASSWORD;


@JsonTypeName("USERNAME_PASSWORD")
public record UsernamePasswordRequest(
        @NotNull @NotEmpty String username,
        @NotNull @NotEmpty String password

) implements SignInRequest {

    @Override
    public AuthType type() {
        return USERNAME_PASSWORD;
    }
}
