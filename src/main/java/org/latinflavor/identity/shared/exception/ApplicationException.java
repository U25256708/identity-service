package org.latinflavor.identity.shared.exception;

import lombok.Setter;
import org.springframework.http.HttpStatus;

public class ApplicationException extends RuntimeException {

    private final ApplicationError error;

    @Setter
    private Object[] args;

    public ApplicationException(ApplicationError error) {
        super(error.getMessage());
        this.error = error;
    }

    public ApplicationException(ApplicationError error, Object... args) {
        super(String.format(error.getMessage(), args));
        this.error = error;
        this.args = args;
    }

    public HttpStatus getHttpStatus() {
        return this.error.getHttpStatus();
    }

    public String getExceptionName() {
        return this.error.getExceptionName();
    }
}
