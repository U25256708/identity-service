package org.latinflavor.identity.adapter.external.customer;

import java.util.UUID;

public record CreateCustomerResponse(
        UUID id,
        UUID userId,
        String email,
        boolean active,
        boolean profileCompleted
) {
}
