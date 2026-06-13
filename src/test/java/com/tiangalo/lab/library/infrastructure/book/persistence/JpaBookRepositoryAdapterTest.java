package com.tiangalo.lab.library.infrastructure.book.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tiangalo.lab.library.application.book.command.SearchBooksQuery;
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
import java.util.List;
import java.util.Locale;
import java.util.Optional;

class JpaBookRepositoryAdapterTest {

    private SpringDataBookRepository repository;
    private Clock fixedClock;
    private BookPersistenceMapper mapper;
    private JpaBookRepositoryAdapter adapter;
    private Instant now;

    @BeforeEach
    void setUp() {
        this.fixedClock = Clock.fixed(Instant.parse("2026-06-09T00:09:00Z"), ZoneOffset.UTC);
        this.repository = mock(SpringDataBookRepository.class);
        this.mapper = new BookPersistenceMapper(fixedClock);
        this.adapter = new JpaBookRepositoryAdapter(repository, mapper);
        this.now = Instant.parse("2026-06-09T00:09:00Z");
    }

    @Test
    void saveShouldPersistBookAndReturnDomainBook() {
        Book book = Book.create(
                "Clean Code",
                "Uncle Bob",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING,
                2019,
                now,
                currentYear());

        when(repository.save(any(BookJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Book savedBook = adapter.save(book);

        assertThat(savedBook.getId()).isEqualTo(book.getId());
        assertThat(savedBook.getTitle()).isEqualTo("Clean Code");
        assertThat(savedBook.getAuthor()).isEqualTo("Uncle Bob");
        assertThat(savedBook.getIsbn()).isEqualTo("9780134494167");
        assertThat(savedBook.getCategory()).isEqualTo(BookCategory.SOFTWARE_ENGINEERING);
        assertThat(savedBook.getPublicationYear()).isEqualTo(2019);
        assertThat(savedBook.getCreatedAt()).isEqualTo(now);
        assertThat(savedBook.getUpdatedAt()).isEqualTo(now);

        verify(repository).save(any(BookJpaEntity.class));
    }

    @Test
    void findByIdShouldReturnExistingBook() {
        Book book = Book.create(
                "Clean Code",
                "Uncle Bob",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING,
                2019,
                now,
                currentYear());

        BookJpaEntity entity = mapper.toEntity(book);
        when(repository.findById(book.getId().value()))
                .thenReturn(Optional.of(entity));

        Optional<Book> foundBook = adapter.findById(book.getId());
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getId()).isEqualTo(book.getId());

        verify(repository).findById(book.getId().value());
    }

    @Test
    void findByIdShouldReturnEmptyWhenBookDoesNotExist() {
        BookId bookId = BookId.newId();
        when(repository.findById(bookId.value()))
                .thenReturn(Optional.empty());
        Optional<Book> bookNotFound = adapter.findById(bookId);
        assertThat(bookNotFound).isEmpty();
        verify(repository).findById(bookId.value());
    }

    @Test
    void searchShouldFilterByTitle() {
        List<BookJpaEntity> books = books();
        SearchBooksQuery query = new SearchBooksQuery(
                "clean",
                null,
                null,
                BookStatus.ACTIVE);
        when(repository.findAll())
                .thenReturn(books);
        List<Book> result = adapter.search(query);
        assertThat(result).allMatch(book -> book.getTitle().toLowerCase(Locale.ROOT).contains("clean"));
        assertThat(result)
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Clean Code", "Clean Architecture");
        verify(repository).findAll();
    }

    @Test
    void searchShouldFilterByAuthor() {
        List<BookJpaEntity> books = books();
        SearchBooksQuery query = new SearchBooksQuery(
                null,
                "Uncle Bob",
                null,
                BookStatus.ACTIVE);
        when(repository.findAll())
                .thenReturn(books);
        List<Book> result = adapter.search(query);
        assertThat(result).allMatch(book -> book.getAuthor().toLowerCase(Locale.ROOT).contains("uncle"));
        assertThat(result)
                .extracting(Book::getAuthor)
                .containsExactlyInAnyOrder("Uncle Bob", "Uncle Bob");
        verify(repository).findAll();
    }

    @Test
    void searchShouldFilterByCategory() {
        List<BookJpaEntity> books = books();
        SearchBooksQuery query = new SearchBooksQuery(
                null,
                null,
                BookCategory.COMPUTER_SCIENCE,
                BookStatus.ACTIVE);
        when(repository.findAll())
                .thenReturn(books);
        List<Book> result = adapter.search(query);
        assertThat(result)
                .extracting(Book::getCategory)
                .containsExactlyInAnyOrder(BookCategory.COMPUTER_SCIENCE);
        verify(repository).findAll();
    }

    @Test
    void searchShouldFilterByStatus() {
        List<BookJpaEntity> books = books();
        SearchBooksQuery query = new SearchBooksQuery(
                null,
                null,
                null,
                BookStatus.ACTIVE);
        when(repository.findAll())
                .thenReturn(books);
        List<Book> result = adapter.search(query);
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(book -> book.getStatus().equals(BookStatus.ACTIVE));
        verify(repository).findAll();
    }

    @Test
    void existsByIsbnShouldDelegateToRepository() {
        when(repository.existsByIsbn("9780134494161"))
                .thenReturn(true);
        boolean result = adapter.existsByIsbn("9780134494161");
        assertThat(result).isTrue();
        verify(repository).existsByIsbn("9780134494161");
    }

    @Test
    void existsByIsbnAndIdNotShouldDelegateToRepository() {
        BookId bookId = BookId.newId();
        when(repository.existsByIsbnAndIdNot("9780134494167", bookId.value()))
                .thenReturn(true);
        boolean result = adapter.existsByIsbnAndIdNot("9780134494167", bookId);
        assertThat(result).isTrue();
        verify(repository).existsByIsbnAndIdNot("9780134494167", bookId.value());
    }

    @Test
    void saveShouldRejectNullBook() {
        assertThatThrownBy(() -> adapter.save(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("book cannot be null");
    }

    @Test
    void findByIdShouldRejectNullBookId() {
        assertThatThrownBy(() -> adapter.findById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("bookId cannot be null");
    }

    @Test
    void searchShouldRejectNullQuery() {
        assertThatThrownBy(() -> adapter.search(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("query cannot be null");
    }

    @Test
    void existsByIsbnShouldRejectNullIsbn() {
        assertThatThrownBy(() -> adapter.existsByIsbn(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("isbn cannot be null");
    }

    @Test
    void existsByIsbnAndIdNotShouldRejectNullIsbn() {
        assertThatThrownBy(() -> adapter.existsByIsbnAndIdNot(null, BookId.newId()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("isbn cannot be null");
    }

    @Test
    void existsByIsbnAndIdNotShouldRejectNullBookId() {
        assertThatThrownBy(() -> adapter.existsByIsbnAndIdNot("9780134494161", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("bookId cannot be null");
    }

    private Integer currentYear() {
        return Year.now(fixedClock).getValue();
    }

    private List<BookJpaEntity> books() {
        return List.of(
                new BookJpaEntity(
                        BookId.newId().value(),
                        "Clean Code",
                        "Uncle Bob",
                        "9780134494167",
                        BookCategory.SOFTWARE_ENGINEERING,
                        2019,
                        BookStatus.ACTIVE,
                        now,
                        now),
                new BookJpaEntity(
                        BookId.newId().value(),
                        "Clean Architecture",
                        "Uncle Bob",
                        "9780134494168",
                        BookCategory.COMPUTER_SCIENCE,
                        2016,
                        BookStatus.ACTIVE,
                        now,
                        now),
                new BookJpaEntity(
                        BookId.newId().value(),
                        "Learn Python",
                        "Guido van Rossum",
                        "9780134494161",
                        BookCategory.SOFTWARE_ENGINEERING,
                        2015,
                        BookStatus.ACTIVE,
                        now,
                        now),
                new BookJpaEntity(
                        BookId.newId().value(),
                        "Old Book",
                        "Old Author",
                        "9780134492473",
                        BookCategory.SOFTWARE_ENGINEERING,
                        2015,
                        BookStatus.INACTIVE,
                        now,
                        now));
    }
}