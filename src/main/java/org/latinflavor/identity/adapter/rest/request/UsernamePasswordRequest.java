package org.latinflavor.identity.adapter.rest.request;

import com.fasterxml.jackson.annotation.JsonTypeName;

import static org.latinflavor.identity.adapter.rest.request.SignInRequest.AuthType.USERNAME_PASSWORD;


@JsonTypeName("USERNAME_PASSWORD")
public record UsernamePasswordRequest(
        String username,
        String password

) implements SignInRequest {

    @Override
    public AuthType type() {
        return USERNAME_PASSWORD;
    }
}
