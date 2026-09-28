package org.latinflavor.identity.application.port.out.external.jwt;

import java.util.Set;

public record IssuedToken(
        String accessToken,
        long expiresIn,
        Set<String> permissions
) {
}
