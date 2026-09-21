package org.latinflavor.identity.domain;

import org.latinflavor.identity.shared.exception.ApplicationError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum OtpErrors implements ApplicationError {

    INVALID_OTP_CODE(HttpStatus.BAD_REQUEST, "An error while creating customer with name %s");

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
