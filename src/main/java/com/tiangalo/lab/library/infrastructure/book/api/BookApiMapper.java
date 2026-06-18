package com.tiangalo.lab.library.infrastructure.book.api;

import com.tiangalo.lab.library.application.book.command.CreateBookCommand;
import com.tiangalo.lab.library.application.book.command.UpdateBookCommand;
import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookCategory;
import com.tiangalo.lab.library.domain.book.model.BookId;
import com.tiangalo.lab.library.infrastructure.book.api.request.CreateBookRequest;
import com.tiangalo.lab.library.infrastructure.book.api.request.UpdateBookRequest;
import com.tiangalo.lab.library.infrastructure.book.api.response.BookResponse;

import java.util.Objects;

public class BookApiMapper {

    public CreateBookCommand toCreateCommand(CreateBookRequest request) {
        Objects.requireNonNull(request, "request cannot be null");
        return new CreateBookCommand(
                request.title(),
                request.author(),
                request.isbn(),
                BookCategory.valueOf(request.category()),
                request.publicationYear()
        );
    }

    public UpdateBookCommand toUpdateCommand(BookId bookId, UpdateBookRequest request) {
        Objects.requireNonNull(bookId, "bookId cannot be null");
        Objects.requireNonNull(request, "request cannot be null");
        return new UpdateBookCommand(
                bookId,
                request.title(),
                request.author(),
                request.isbn(),
                BookCategory.valueOf(request.category()),
                request.publicationYear()
        );
    }

    public BookResponse toResponse(Book book) {
        Objects.requireNonNull(book, "book cannot be null");
        return new BookResponse(
                book.getId().value(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCategory().name(),
                book.getPublicationYear(),
                book.getStatus().name(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}