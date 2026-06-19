package com.tiangalo.lab.library.infrastructure.book.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tiangalo.lab.library.application.book.command.CreateBookCommand;
import com.tiangalo.lab.library.application.book.port.in.CreateBookUseCase;
import com.tiangalo.lab.library.application.book.port.in.GetBookByIdUseCase;
import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookCategory;
import com.tiangalo.lab.library.domain.book.model.BookId;
import com.tiangalo.lab.library.domain.book.model.BookStatus;
import com.tiangalo.lab.library.infrastructure.book.api.request.CreateBookRequest;
import com.tiangalo.lab.library.infrastructure.book.api.response.BookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Clock;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneOffset;

class BookControllerTest {

    private BookController controller;

    private CreateBookUseCase createBookUseCase;
    private GetBookByIdUseCase getBookByIdUseCase;
    private BookApiMapper mapper;
    private Instant now;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        this.mapper = new BookApiMapper();
        this.fixedClock = Clock.fixed(Instant.parse("2026-06-17T23:48:00Z"), ZoneOffset.UTC);
        this.now = Instant.parse("2026-06-17T23:48:00Z");
        this.createBookUseCase = mock(CreateBookUseCase.class);
        this.getBookByIdUseCase = mock(GetBookByIdUseCase.class);
        this.controller = new BookController(createBookUseCase, getBookByIdUseCase, mapper);
    }

    @Test
    void createBookShouldReturnCreatedBook() {
        Book book = Book.create(
                "Clean Code",
                "Robert C Martin",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING,
                2019,
                now,
                currentYear()
        );
        CreateBookRequest request = new CreateBookRequest(
                "Clean Code",
                "Robert C Martin",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING.name(),
                2019
        );
        when(createBookUseCase.createBook(any(CreateBookCommand.class)))
                .thenReturn(book);
        ResponseEntity<BookResponse> response = controller.createBook(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(book.getId().value());
        assertThat(response.getBody().title()).isEqualTo("Clean Code");
        assertThat(response.getBody().author()).isEqualTo("Robert C Martin");
        assertThat(response.getBody().isbn()).isEqualTo("9780134494167");
        assertThat(BookCategory.valueOf(response.getBody().category())).isEqualTo(BookCategory.SOFTWARE_ENGINEERING);
        assertThat(BookStatus.valueOf(response.getBody().status())).isEqualTo(BookStatus.ACTIVE);
        assertThat(response.getBody().createdAt()).isEqualTo(now);
        assertThat(response.getBody().updatedAt()).isEqualTo(now);
        verify(createBookUseCase).createBook(any(CreateBookCommand.class));
    }

    @Test
    void getBookByIdShouldReturnBookResponse() {
        Book book = Book.create(
                "Clean Code",
                "Robert C Martin",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING,
                2019,
                now,
                currentYear()
        );

        BookId bookId = book.getId();

        when(getBookByIdUseCase.getBookById(bookId))
            .thenReturn(book);

        ResponseEntity<BookResponse> response = controller.getBookById(bookId.value());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(bookId.value());
        assertThat(response.getBody().title()).isEqualTo("Clean Code");
        assertThat(response.getBody().author()).isEqualTo("Robert C Martin");
        assertThat(response.getBody().isbn()).isEqualTo("9780134494167");
        assertThat(BookCategory.valueOf(response.getBody().category())).isEqualTo(BookCategory.SOFTWARE_ENGINEERING);
        assertThat(response.getBody().publicationYear()).isEqualTo(2019);
        assertThat(BookStatus.valueOf(response.getBody().status())).isEqualTo(BookStatus.ACTIVE);
        assertThat(response.getBody().createdAt()).isEqualTo(now);
        assertThat(response.getBody().updatedAt()).isEqualTo(now);

        verify(getBookByIdUseCase).getBookById(bookId);
    }

    private Integer currentYear() {
        return Year.now(fixedClock).getValue();
    }
}