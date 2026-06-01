package com.tiangalo.lab.library.application.book.service;

import java.time.Clock;
import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.tiangalo.lab.library.application.book.command.CreateBookCommand;
import com.tiangalo.lab.library.application.book.command.SearchBooksQuery;
import com.tiangalo.lab.library.application.book.command.UpdateBookCommand;
import com.tiangalo.lab.library.application.book.exception.BookNotFoundException;
import com.tiangalo.lab.library.application.book.exception.DuplicatedIsbnException;
import com.tiangalo.lab.library.application.book.exception.InvalidBookCommandException;
import com.tiangalo.lab.library.application.book.port.in.CreateBookUseCase;
import com.tiangalo.lab.library.application.book.port.in.DeactivateBookUseCase;
import com.tiangalo.lab.library.application.book.port.in.GetBookByIdUseCase;
import com.tiangalo.lab.library.application.book.port.in.SearchBooksUseCase;
import com.tiangalo.lab.library.application.book.port.in.UpdateBookUseCase;
import com.tiangalo.lab.library.application.book.port.out.BookRepositoryPort;
import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookId;
import com.tiangalo.lab.library.domain.book.model.BookStatus;

public class BookApplicationService
        implements CreateBookUseCase, GetBookByIdUseCase, SearchBooksUseCase, UpdateBookUseCase, DeactivateBookUseCase {

    private final BookRepositoryPort repository;
    private final Clock clock;

    public BookApplicationService(BookRepositoryPort repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository cannot be null");
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    @Override
    public Book createBook(CreateBookCommand command) {
        requireNonNull(command, "command");
        Book book = Book.create(
                command.title(),
                command.author(),
                command.isbn(),
                command.category(),
                command.publicationYear(),
                now(),
                currentYear());
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new DuplicatedIsbnException("isbn: " + book.getIsbn() + " already exists");
        }
        return repository.save(book);
    }

    @Override
    public Book getBookById(BookId bookId) {
        return getRequiredBook(bookId);
    }

    @Override
    public List<Book> searchBooks(SearchBooksQuery query) {
        SearchBooksQuery effectiveQuery;
        if (query == null) {
            effectiveQuery = new SearchBooksQuery(
                    null,
                    null,
                    null,
                    BookStatus.ACTIVE);
        } else if (query.status() == null) {
            effectiveQuery = new SearchBooksQuery(
                    query.title(),
                    query.author(),
                    query.category(),
                    BookStatus.ACTIVE);
        } else {
            effectiveQuery = query;
        }
        return repository.search(effectiveQuery);
    }

    @Override
    public Book updateBook(UpdateBookCommand command) {
        requireNonNull(command, "command");
        Book book = getRequiredBook(command.bookId());
        if (repository.existsByIsbnAndIdNot(command.isbn(), command.bookId())) {
            throw new DuplicatedIsbnException("isbn: " + command.isbn() + " already exists");
        }
        book.updateDetails(
                command.title(),
                command.author(),
                command.isbn(),
                command.category(),
                command.publicationYear(),
                now(),
                currentYear());
        return repository.save(book);
    }

    @Override
    public Book deactivateBook(BookId bookId) {
        Book book = getRequiredBook(bookId);
        book.deactivate(now());
        return repository.save(book);
    }

    private Book getRequiredBook(BookId bookId) {
        requireNonNull(bookId, "bookId");
        Optional<Book> optionalBook = repository.findById(bookId);
        return optionalBook.orElseThrow(() -> new BookNotFoundException("Book not found"));
    }

    private Instant now() {
        return Instant.now(clock);
    }

    private Integer currentYear() {
        return Year.now(clock).getValue();
    }

    private static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new InvalidBookCommandException(fieldName + " is required");
        }
    }
}