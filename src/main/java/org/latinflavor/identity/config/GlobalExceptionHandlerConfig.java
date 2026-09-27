package org.latinflavor.identity.config;

import jakarta.servlet.http.HttpServletRequest;
import org.latinflavor.identity.shared.exception.ApplicationException;
import org.latinflavor.identity.shared.exception.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.latinflavor.identity.shared.constant.MessageConstants.DEFAULT_INVALID_REQUEST_ERROR_MESSAGE;
import static org.latinflavor.identity.shared.constant.MessageConstants.INVALID_REQUEST;
import static org.latinflavor.identity.shared.exception.ServerResponse.error;


@RestControllerAdvice
public class GlobalExceptionHandlerConfig {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ServerResponse<Object>> handleGeneralException(ApplicationException e, HttpServletRequest request) {
        return error(e.getExceptionName(), e.getHttpStatus(), e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ServerResponse<Object>> handleUnreadableRequest(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        return error(INVALID_REQUEST, HttpStatus.BAD_REQUEST,
                DEFAULT_INVALID_REQUEST_ERROR_MESSAGE, request.getRequestURI());
    }

}
