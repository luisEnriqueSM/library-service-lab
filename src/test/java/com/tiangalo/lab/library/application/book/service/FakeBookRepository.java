package com.tiangalo.lab.library.application.book.service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.tiangalo.lab.library.application.book.command.SearchBooksQuery;
import com.tiangalo.lab.library.application.book.exception.InvalidBookCommandException;
import com.tiangalo.lab.library.application.book.port.out.BookRepositoryPort;
import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookCategory;
import com.tiangalo.lab.library.domain.book.model.BookId;
import com.tiangalo.lab.library.domain.book.model.BookStatus;

public class FakeBookRepository implements BookRepositoryPort {

    private final Map<BookId, Book> books = new HashMap<>();

    @Override
    public Book save(Book book) {
        requireNonNull(book, "book");
        books.put(book.getId(), book);
        return book;
    }

    @Override
    public Optional<Book> findById(BookId bookId) {
        return Optional.ofNullable(books.get(bookId));
    }

    @Override
    public List<Book> search(SearchBooksQuery query) {
        return books.values().stream()
                .filter(book -> matchesTitle(book, query.title()))
                .filter(book -> matchesAuthor(book, query.author()))
                .filter(book -> matchesCategory(book, query.category()))
                .filter(book -> matchesStatus(book, query.status()))
                .toList();
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return books.values().stream()
                .anyMatch(book -> book.getIsbn().equals(isbn));
    }

    @Override
    public boolean existsByIsbnAndIdNot(String isbn, BookId bookId) {
        return books.values().stream()
                .anyMatch(book -> book.getIsbn().equals(isbn)
                        && !book.getId().equals(bookId));
    }

    private boolean matchesTitle(Book book, String title) {
        return title == null || title.isBlank() ||
                book.getTitle().toLowerCase(Locale.ROOT).contains(title.toLowerCase(Locale.ROOT));
    }

    private boolean matchesAuthor(Book book, String author) {
        return author == null || author.isBlank() ||
                book.getAuthor().toLowerCase(Locale.ROOT).contains(author.toLowerCase(Locale.ROOT));
    }

    private boolean matchesCategory(Book book, BookCategory category) {
        return category == null || book.getCategory() == category;
    }

    private boolean matchesStatus(Book book, BookStatus status) {
        return status == null || book.getStatus() == status;
    }

    private static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new InvalidBookCommandException(fieldName + " is required");
        }
    }
}
