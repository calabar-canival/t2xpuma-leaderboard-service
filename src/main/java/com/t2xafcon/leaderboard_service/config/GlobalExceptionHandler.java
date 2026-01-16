package com.t2xafcon.leaderboard_service.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponseBody<Object>> handleResponseStatusException(
            ResponseStatusException e) {

        Error error = objectMapper.readValue(e.getReason(), Error.class);

        List<String> errors = new ArrayList<>();
        errors.add(error.toString());

        return ResponseEntity
                .status(e.getStatusCode())
                .body(ApiResponseBody.error(
                        e.getStatusCode().value(),
                        errors
                ));
    }


    private record Error(
            Boolean isSuccessful,
            String message
    ){
        @Override
        public String toString() {
            return '{' + "isSuccessful=" + isSuccessful + ", message=" + message + '}';
        }
    }
}
