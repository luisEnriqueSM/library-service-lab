package com.tiangalo.lab.library.application.book.command;

import java.util.Objects;

import com.tiangalo.lab.library.domain.book.model.BookCategory;
import com.tiangalo.lab.library.domain.book.model.BookId;

public record UpdateBookCommand(
        BookId bookId,
        String title,
        String author,
        String isbn,
        BookCategory category,
        Integer publicationYear) {
    
    public UpdateBookCommand {
        Objects.requireNonNull(bookId, "bookId cannot be null");
    }
}