package org.latinflavor.identity.adapter.external.customer;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.application.port.out.external.customer.CustomerLookupPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerServiceAdapter implements CustomerLookupPort {

    private final CustomerServiceClient client;

    @Override
    public Optional<UUID> findEligibleAuthenticatedCustomerId(UUID authenticatedUserId) {
        requireAuthenticatedUser(authenticatedUserId);
        try {
            CustomerEligibilityResponse response = client.getBookingEligibility();
            return response != null && response.isEligible()
                    ? Optional.of(response.customerId())
                    : Optional.empty();
        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();
        }
    }

    private void requireAuthenticatedUser(UUID authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new IllegalArgumentException("authenticatedUserId must not be null");
        }
    }
}
