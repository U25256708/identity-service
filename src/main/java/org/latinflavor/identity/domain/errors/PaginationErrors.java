package org.latinflavor.identity.domain.errors;

import org.latinflavor.identity.shared.exception.ApplicationError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum PaginationErrors implements ApplicationError {

    INVALID_PAGE(HttpStatus.BAD_REQUEST, "Page index must be greater than or equal to zero"),
    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "Page size must be between 1 and 100"),
    INVALID_SORT_FORMAT(HttpStatus.BAD_REQUEST, "Invalid sort format: %s. Expected 'field' or 'field,direction'"),
    UNSUPPORTED_SORT_FIELD(HttpStatus.BAD_REQUEST, "Unsupported user sort field: %s");

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
