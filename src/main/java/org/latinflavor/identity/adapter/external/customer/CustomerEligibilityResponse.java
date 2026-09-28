package org.latinflavor.identity.adapter.external.customer;

import java.util.UUID;

public record CustomerEligibilityResponse(
        UUID customerId,
        boolean active,
        boolean profileCompleted
) {
    boolean isEligible() {
        return customerId != null && active && profileCompleted;
    }
}
