package org.latinflavor.identity.adapter.rest.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UsernamePasswordRequest.class),
        @JsonSubTypes.Type(value = OneTimePasswordRequest.class)
})
public sealed interface SignInRequest permits UsernamePasswordRequest, OneTimePasswordRequest {

    AuthType type();

    enum AuthType {
        USERNAME_PASSWORD,
        OTP,
    }
}
