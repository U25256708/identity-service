package org.latinflavor.identity.application.port.out.external.customer;

public record CreateCustomerCommand(
        String email,
        String accessToken
) {
}
