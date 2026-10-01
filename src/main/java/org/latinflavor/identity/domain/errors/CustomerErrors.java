package org.latinflavor.identity.domain.errors;

import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.shared.exception.ApplicationError;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CustomerErrors implements ApplicationError {

    CUSTOMER_REGISTRATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,
            "The customer could not be registered for email %s");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getExceptionName() {
        return this.name();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
