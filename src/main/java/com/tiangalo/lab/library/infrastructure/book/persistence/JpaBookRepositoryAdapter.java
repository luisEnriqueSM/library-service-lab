package com.tiangalo.lab.library.infrastructure.book.persistence;

import com.tiangalo.lab.library.application.book.command.SearchBooksQuery;
import com.tiangalo.lab.library.application.book.port.out.BookRepositoryPort;
import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookCategory;
import com.tiangalo.lab.library.domain.book.model.BookId;
import com.tiangalo.lab.library.domain.book.model.BookStatus;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class JpaBookRepositoryAdapter implements BookRepositoryPort {

    private final SpringDataBookRepository repository;
    private final BookPersistenceMapper mapper;

    public JpaBookRepositoryAdapter(SpringDataBookRepository repository, BookPersistenceMapper mapper) {
        this.repository = Objects.requireNonNull(repository, "repository cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "mapper cannot be null");
    }

    @Override
    public Book save(Book book) {
        Objects.requireNonNull(book, "book cannot be null");
        BookJpaEntity entity = repository.save(mapper.toEntity(book));
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Book> findById(BookId bookId) {
        Objects.requireNonNull(bookId, "bookId cannot be null");
        return repository.findById(bookId.value()).map(mapper::toDomain);
    }

    @Override
    public List<Book> search(SearchBooksQuery query) {
        Objects.requireNonNull(query, "query cannot be null");
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .filter(book -> matchesTitle(book, query.title()))
                .filter(book -> matchesAuthor(book, query.author()))
                .filter(book -> matchesCategory(book, query.category()))
                .filter(book -> matchesStatus(book, query.status()))
                .toList();
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        Objects.requireNonNull(isbn, "isbn cannot be null");
        return repository.existsByIsbn(isbn);
    }

    @Override
    public boolean existsByIsbnAndIdNot(String isbn, BookId bookId) {
        Objects.requireNonNull(isbn, "isbn cannot be null");
        Objects.requireNonNull(bookId, "bookId cannot be null");
        return repository.existsByIsbnAndIdNot(isbn, bookId.value());
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
}