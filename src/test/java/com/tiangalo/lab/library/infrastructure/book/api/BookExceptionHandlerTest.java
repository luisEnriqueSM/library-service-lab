package com.tiangalo.lab.library.infrastructure.book.api;

import com.tiangalo.lab.library.application.book.exception.BookNotFoundException;
import com.tiangalo.lab.library.application.book.exception.DuplicatedIsbnException;
import com.tiangalo.lab.library.application.book.exception.InvalidBookCommandException;
import com.tiangalo.lab.library.domain.book.exception.InvalidBookException;
import com.tiangalo.lab.library.infrastructure.book.api.response.ApiErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class BookExceptionHandlerTest {

    private BookExceptionHandler handler;
    private Instant now;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-06-17T23:48:00Z"), ZoneOffset.UTC);
        this.now = Instant.parse("2026-06-17T23:48:00Z");
        this.handler = new BookExceptionHandler(fixedClock);
    }

    @Test
    void handleBookNotFoundShouldReturnNotFound() {
        BookNotFoundException exception = new BookNotFoundException("Book not found");
        ResponseEntity<ApiErrorResponse> response = handler.handleBookNotFound(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("BOOK_NOT_FOUND");
        assertThat(response.getBody().message()).isEqualTo("Book not found");
        assertThat(response.getBody().timestamp()).isEqualTo(now);
    }

    @Test
    void handleDuplicatedIsbnShouldReturnConflict() {
        DuplicatedIsbnException exception = new DuplicatedIsbnException("Duplicated isbn");
        ResponseEntity<ApiErrorResponse> response = handler.handleDuplicatedIsbn(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("DUPLICATED_ISBN");
        assertThat(response.getBody().message()).isEqualTo("Duplicated isbn");
        assertThat(response.getBody().timestamp()).isEqualTo(now);
    }

    @Test
    void handleInvalidBookCommandShouldReturnBadRequest() {
        InvalidBookCommandException exception = new InvalidBookCommandException("Invalid book command");
        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().message()).isEqualTo("Invalid book command");
        assertThat(response.getBody().timestamp()).isEqualTo(now);
    }

    @Test
    void handleInvalidBookShouldReturnBadRequest() {
        InvalidBookException exception = new InvalidBookException("Title cannot be blank");
        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().message()).isEqualTo("Title cannot be blank");
        assertThat(response.getBody().timestamp()).isEqualTo(now);
    }

    @Test
    void handleIllegalArgumentShouldReturnBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid enum value");
        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().message()).isEqualTo("Invalid enum value");
        assertThat(response.getBody().timestamp()).isEqualTo(now);
    }
}
