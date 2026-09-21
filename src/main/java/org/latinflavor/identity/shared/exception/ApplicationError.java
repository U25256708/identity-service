package org.latinflavor.identity.shared.exception;

import org.springframework.http.HttpStatus;

public interface ApplicationError {
    String getExceptionName();

    HttpStatus getHttpStatus();

    String getMessage();

}
