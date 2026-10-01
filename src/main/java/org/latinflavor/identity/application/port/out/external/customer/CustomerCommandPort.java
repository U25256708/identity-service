package org.latinflavor.identity.application.port.out.external.customer;

public interface CustomerCommandPort {
    RegisteredCustomer create(CreateCustomerCommand command);

}
