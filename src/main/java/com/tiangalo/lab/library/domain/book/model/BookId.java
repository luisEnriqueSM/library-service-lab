package com.tiangalo.lab.library.domain.book.model;

import java.util.Objects;
import java.util.UUID;

public record BookId(UUID value) {

    public BookId {
        Objects.requireNonNull(value, "Book id cannot be null");
    }

    public static BookId newId() {
        return new BookId(UUID.randomUUID());
    }

    public static BookId from(UUID value) {
        return new BookId(value);
    }
}
