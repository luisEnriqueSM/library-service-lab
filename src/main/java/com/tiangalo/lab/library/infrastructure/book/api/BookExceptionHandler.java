package com.tiangalo.lab.library.infrastructure.book.api;

import com.tiangalo.lab.library.application.book.exception.BookNotFoundException;
import com.tiangalo.lab.library.application.book.exception.DuplicatedIsbnException;
import com.tiangalo.lab.library.application.book.exception.InvalidBookCommandException;
import com.tiangalo.lab.library.domain.book.exception.InvalidBookException;
import com.tiangalo.lab.library.infrastructure.book.api.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.Instant;

@RestControllerAdvice
public class BookExceptionHandler {

    private final Clock clock;

    public BookExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBookNotFound(BookNotFoundException exception) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "BOOK_NOT_FOUND",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DuplicatedIsbnException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicatedIsbn(DuplicatedIsbnException exception) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "DUPLICATED_ISBN",
                exception.getMessage()
        );
    }

    @ExceptionHandler({
            InvalidBookCommandException.class,
            InvalidBookException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(RuntimeException exception) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationError(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Invalid request");
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                message
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String code,
            String message
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                code,
                message,
                Instant.now(clock)
        );
        return new ResponseEntity<>(response, status);
    }
}