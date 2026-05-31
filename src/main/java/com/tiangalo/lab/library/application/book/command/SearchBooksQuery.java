package com.tiangalo.lab.library.application.book.command;

import com.tiangalo.lab.library.domain.book.model.BookCategory;
import com.tiangalo.lab.library.domain.book.model.BookStatus;

public record SearchBooksQuery(
        String title,
        String author,
        BookCategory category,
        BookStatus status) {
}