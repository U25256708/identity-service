package org.latinflavor.identity.application.port.out.external.customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerLookupPort {
    Optional<UUID> findEligibleAuthenticatedCustomerId(UUID authenticatedUserId);

}
