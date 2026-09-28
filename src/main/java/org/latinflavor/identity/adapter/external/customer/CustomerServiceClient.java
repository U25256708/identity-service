package org.latinflavor.identity.adapter.external.customer;

import org.springframework.http.MediaType;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(value = "/customer-service/v1", accept = MediaType.APPLICATION_JSON_VALUE)
public interface CustomerServiceClient {

    @GetExchange("/customers/me")
    CustomerResponse getCurrentCustomer();

    @GetExchange("/customers/me/booking-eligibility")
    CustomerEligibilityResponse getBookingEligibility();
}
