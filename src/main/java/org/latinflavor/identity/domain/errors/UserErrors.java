package org.latinflavor.identity.domain.errors;

import org.latinflavor.identity.shared.exception.ApplicationError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrors implements ApplicationError {

    USER_IS_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "The user with email %s is not active"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid username or password"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User %s was not found"),
    USER_NOT_INTERNAL(HttpStatus.BAD_REQUEST, "User %s is not an internal user"),
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "A user with email %s already exists"),
    USER_USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "A user with username %s already exists"),
    USER_DNI_ALREADY_EXISTS(HttpStatus.CONFLICT, "A user with DNI %s already exists"),
    ROLE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Role %s was not found or is inactive"),
    PERMISSION_NOT_FOUND(HttpStatus.BAD_REQUEST, "Permission %s was not found or is inactive");

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
