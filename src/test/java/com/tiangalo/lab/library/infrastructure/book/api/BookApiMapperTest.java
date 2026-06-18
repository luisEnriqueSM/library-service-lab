package com.tiangalo.lab.library.infrastructure.book.api;

import com.tiangalo.lab.library.application.book.command.CreateBookCommand;
import com.tiangalo.lab.library.application.book.command.UpdateBookCommand;
import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookCategory;
import com.tiangalo.lab.library.domain.book.model.BookId;
import com.tiangalo.lab.library.domain.book.model.BookStatus;
import com.tiangalo.lab.library.infrastructure.book.api.request.CreateBookRequest;
import com.tiangalo.lab.library.infrastructure.book.api.request.UpdateBookRequest;
import com.tiangalo.lab.library.infrastructure.book.api.response.BookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookApiMapperTest {

    private BookApiMapper mapper;
    private Instant now;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        mapper = new BookApiMapper();
        this.fixedClock = Clock.fixed(Instant.parse("2026-06-17T23:48:00Z"), ZoneOffset.UTC);
        this.now = Instant.parse("2026-06-17T23:48:00Z");
    }

    @Test
    void toCreateCommandShouldMapRequestToCommand() {
        CreateBookRequest request = new CreateBookRequest(
                "Clean Code",
                "Robert C Martin",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING.name(),
                2019
            );
        CreateBookCommand command = mapper.toCreateCommand(request);
        assertThat(command.title()).isEqualTo("Clean Code");
        assertThat(command.author()).isEqualTo("Robert C Martin");
        assertThat(command.isbn()).isEqualTo("9780134494167");
        assertThat(command.category()).isEqualTo(BookCategory.SOFTWARE_ENGINEERING);
        assertThat(command.publicationYear()).isEqualTo(2019);
    }

    @Test
    void toUpdateCommandShouldMapRequestToCommand() {
        UpdateBookRequest request = new UpdateBookRequest(
                "Clean Code",
                "Robert C Martin",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING.name(),
                2019
            );
        BookId bookId = BookId.newId();
        UpdateBookCommand command = mapper.toUpdateCommand(bookId, request);
        assertThat(command.bookId()).isEqualTo(bookId);
        assertThat(command.title()).isEqualTo("Clean Code");
        assertThat(command.author()).isEqualTo("Robert C Martin");
        assertThat(command.isbn()).isEqualTo("9780134494167");
        assertThat(command.category()).isEqualTo(BookCategory.SOFTWARE_ENGINEERING);
        assertThat(command.publicationYear()).isEqualTo(2019);
    }

    @Test
    void toResponseShouldMapBookToResponse() {
        Book book = Book.create(
                "Clean Code",
                "Robert C Martin",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING,
                2019,
                now,
                currentYear()
            );
        BookResponse response = mapper.toResponse(book);
        assertThat(response.id()).isEqualTo(book.getId().value());
        assertThat(response.title()).isEqualTo("Clean Code");
        assertThat(response.author()).isEqualTo("Robert C Martin");
        assertThat(response.isbn()).isEqualTo("9780134494167");
        assertThat(BookCategory.valueOf(response.category())).isEqualTo(BookCategory.SOFTWARE_ENGINEERING);
        assertThat(response.publicationYear()).isEqualTo(2019);
        assertThat(BookStatus.valueOf(response.status())).isEqualTo(BookStatus.ACTIVE);
        assertThat(response.createdAt()).isEqualTo(now);
        assertThat(response.updatedAt()).isEqualTo(now);
    }

    @Test
    void toCreateCommandShouldRejectNullRequest() {
        assertThatThrownBy(() -> mapper.toCreateCommand(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("request cannot be null");
    }

    @Test
    void toUpdateCommandShouldRejectNullBookId() {
        UpdateBookRequest request = new UpdateBookRequest(
                "Clean Code",
                "Robert C Martin",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING.name(),
                2019
        );
        assertThatThrownBy(() -> mapper.toUpdateCommand(null, request))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("bookId cannot be null");
    }

    @Test
    void toUpdateCommandShouldRejectNullRequest() {
        assertThatThrownBy(() -> mapper.toUpdateCommand(BookId.newId(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("request cannot be null");
    }

    @Test
    void toResponseShouldRejectNullBook() {
        assertThatThrownBy(() -> mapper.toResponse(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("book cannot be null");
    }

    private Integer currentYear() {
        return Year.now(fixedClock).getValue();
    }
}
