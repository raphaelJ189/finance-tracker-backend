package com.financetracker.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ErrorResponse {

    private final int status;
    private final String message;
    private final List<String> errors;
    private final LocalDateTime timestamp;

    // Private constructor
    private ErrorResponse(int status,
                          String message,
                          List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    // For simple errors — no field level errors
    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message, null);
    }

    // For validation errors — with field level errors
    public static ErrorResponse of(int status,
                                   String message,
                                   List<String> errors) {
        return new ErrorResponse(status, message, errors);
    }
}