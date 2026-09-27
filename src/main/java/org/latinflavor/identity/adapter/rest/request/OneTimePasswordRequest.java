package org.latinflavor.identity.adapter.rest.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static org.latinflavor.identity.adapter.rest.request.SignInRequest.AuthType.OTP;


@JsonTypeName("OTP")
public record OneTimePasswordRequest(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{6}") String code

) implements SignInRequest {

    @Override
    public AuthType type() {
        return OTP;
    }
}
