package org.latinflavor.identity.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.latinflavor.identity.shared.exception.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ServerResponseAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private static final String INVALID_TOKEN = "INVALID_TOKEN";
    private static final String UNAUTHORIZED = "UNAUTHORIZED";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid or expired JWT";
    private static final String UNAUTHORIZED_MESSAGE = "Authentication is required";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        boolean invalidToken = exception instanceof BadCredentialsException;
        String exceptionName = invalidToken ? INVALID_TOKEN : UNAUTHORIZED;
        String message = invalidToken ? INVALID_TOKEN_MESSAGE : UNAUTHORIZED_MESSAGE;

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ServerResponse<Object> body = ServerResponse.error(
                exceptionName,
                HttpStatus.UNAUTHORIZED,
                message,
                request.getRequestURI()
        ).getBody();

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
