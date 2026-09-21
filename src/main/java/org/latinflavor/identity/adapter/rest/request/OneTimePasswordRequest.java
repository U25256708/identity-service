package org.latinflavor.identity.adapter.rest.request;

import com.fasterxml.jackson.annotation.JsonTypeName;

import static org.latinflavor.identity.adapter.rest.request.SignInRequest.AuthType.OTP;


@JsonTypeName("OTP")
public record OneTimePasswordRequest(
        String email,
        String code

) implements SignInRequest {

    @Override
    public AuthType type() {
        return OTP;
    }
}
