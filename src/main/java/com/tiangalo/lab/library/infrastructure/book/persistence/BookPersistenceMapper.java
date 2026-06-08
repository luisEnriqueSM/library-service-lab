package com.tiangalo.lab.library.infrastructure.book.persistence;

import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookId;

import java.time.Clock;
import java.time.Year;
import java.util.Objects;

public class BookPersistenceMapper {

    private final Clock clock;

    public BookPersistenceMapper(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    public Book toDomain(BookJpaEntity bookJpaEntity) {
        Objects.requireNonNull(bookJpaEntity, "bookJpaEntity cannot be null");
        BookId bookId = BookId.from(bookJpaEntity.getId());
        return Book.restore(
                bookId,
                bookJpaEntity.getTitle(),
                bookJpaEntity.getAuthor(),
                bookJpaEntity.getIsbn(),
                bookJpaEntity.getCategory(),
                bookJpaEntity.getPublicationYear(),
                bookJpaEntity.getStatus(),
                bookJpaEntity.getCreatedAt(),
                bookJpaEntity.getUpdatedAt(),
                currentYear());
    }

    public BookJpaEntity toEntity(Book book) {
        Objects.requireNonNull(book, "book cannot be null");
        return new BookJpaEntity(book.getId().value(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCategory(),
                book.getPublicationYear(),
                book.getStatus(),
                book.getCreatedAt(),
                book.getUpdatedAt());
    }

    private Integer currentYear() {
        return Year.now(clock).getValue();
    }
}
