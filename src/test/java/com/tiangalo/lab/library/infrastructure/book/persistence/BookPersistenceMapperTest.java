package com.tiangalo.lab.library.infrastructure.book.persistence;

import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookCategory;
import com.tiangalo.lab.library.domain.book.model.BookId;
import com.tiangalo.lab.library.domain.book.model.BookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookPersistenceMapperTest {

    private BookPersistenceMapper mapper;
    private Clock fixedClock;
    private Instant now;

    @BeforeEach
    void setup() {
        this.fixedClock = Clock.fixed(Instant.parse("2026-06-07T23:48:00Z"), ZoneOffset.UTC);
        this.mapper = new BookPersistenceMapper(fixedClock);
        this.now = Instant.parse("2026-06-07T23:48:00Z");
    }

    @Test
    void toDomainShouldRestoreBookFromJpaEntity() {
        BookJpaEntity bookJpaEntity = new BookJpaEntity(
                BookId.newId().value(),
                "Clean Code",
                "Robert C Martin",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING,
                2019,
                BookStatus.ACTIVE,
                now,
                now);
        Book book = mapper.toDomain(bookJpaEntity);
        assertThat(book.getId()).isEqualTo(BookId.from(bookJpaEntity.getId()));
        assertThat(book.getTitle()).isEqualTo("Clean Code");
        assertThat(book.getAuthor()).isEqualTo("Robert C Martin");
        assertThat(book.getIsbn()).isEqualTo("9780134494167");
        assertThat(book.getCategory()).isEqualTo(BookCategory.SOFTWARE_ENGINEERING);
        assertThat(book.getPublicationYear()).isEqualTo(2019);
        assertThat(book.getStatus()).isEqualTo(BookStatus.ACTIVE);
        assertThat(book.getCreatedAt()).isEqualTo(now);
        assertThat(book.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toEntityShouldMapBookToJpaEntity() {
        Book book = Book.create(
                "Clean Code",
                "Robert C Martin",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING,
                2019,
                now,
                currentYear());
        BookJpaEntity bookJpaEntity = mapper.toEntity(book);
        assertThat(bookJpaEntity.getId()).isEqualTo(book.getId().value());
        assertThat(bookJpaEntity.getTitle()).isEqualTo("Clean Code");
        assertThat(bookJpaEntity.getAuthor()).isEqualTo("Robert C Martin");
        assertThat(bookJpaEntity.getIsbn()).isEqualTo("9780134494167");
        assertThat(bookJpaEntity.getCategory()).isEqualTo(BookCategory.SOFTWARE_ENGINEERING);
        assertThat(bookJpaEntity.getPublicationYear()).isEqualTo(2019);
        assertThat(bookJpaEntity.getStatus()).isEqualTo(BookStatus.ACTIVE);
        assertThat(bookJpaEntity.getCreatedAt()).isEqualTo(now);
        assertThat(bookJpaEntity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toDomainShouldRejectNullEntity() {
        assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("bookJpaEntity cannot be null");
    }

    @Test
    void toEntityShouldRejectNullBook() {
        assertThatThrownBy(() -> mapper.toEntity(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("book cannot be null");
    }

    private Integer currentYear() {
        return Year.now(fixedClock).getValue();
    }
}
