package org.latinflavor.identity.application.port.out.external;

import java.util.Set;
import java.util.UUID;

public record TokenSubject(
        UUID userId,
        String email,
        Set<String> roles,
        Set<String> permissions,
        Integer tokenVersion
) {
}
