package com.tiangalo.lab.library.application.book.command;

import java.util.Objects;

import com.tiangalo.lab.library.domain.book.model.BookCategory;

public record CreateBookCommand(
                String title,
                String author,
                String isbn,
                BookCategory category,
                Integer publicationYear) {
        public CreateBookCommand {
                Objects.requireNonNull(isbn, "isbn cannot be null");
        }
}