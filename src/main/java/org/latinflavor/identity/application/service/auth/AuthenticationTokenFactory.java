package org.latinflavor.identity.application.service.auth;

import lombok.experimental.UtilityClass;
import org.latinflavor.identity.adapter.rest.request.OneTimePasswordRequest;
import org.latinflavor.identity.adapter.rest.request.SignInRequest;
import org.latinflavor.identity.adapter.rest.request.UsernamePasswordRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@UtilityClass
public class AuthenticationTokenFactory {

    public Authentication create(SignInRequest request) {
        return switch (request) {
            case UsernamePasswordRequest r -> new UsernamePasswordAuthenticationToken(r.username(), r.password());
            case OneTimePasswordRequest r -> new OneTimePasswordAuthenticationToken(r.email(), r.code());
        };
    }
}
