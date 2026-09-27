package org.latinflavor.identity.shared.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ServerResponse<T>(

        String exceptionName,
        String message,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime timestamp,

        String path,
        String userId,
        T data
) {

    public static <T> ResponseEntity<ServerResponse<T>> success(HttpStatus status, String userId, T data) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ServerResponse.<T>builder()
                        .timestamp(LocalDateTime.now())
                        .userId(userId)
                        .data(data)
                        .build());
    }

    public static <T> ResponseEntity<ServerResponse<T>> error(String exceptionName, HttpStatus status, String message, String requestUri) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ServerResponse.<T>builder()
                        .exceptionName(exceptionName)
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .path(requestUri)
                        .build());
    }

    public static <T> ResponseEntity<ServerResponse<T>> badRequest(String message, String requestUri, T data) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ServerResponse.<T>builder()
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .path(requestUri)
                        .data(data)
                        .build());
    }
}