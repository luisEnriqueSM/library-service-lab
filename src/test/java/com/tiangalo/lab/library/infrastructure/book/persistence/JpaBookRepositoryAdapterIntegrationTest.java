package com.tiangalo.lab.library.infrastructure.book.persistence;

import com.tiangalo.lab.library.application.book.command.SearchBooksQuery;
import com.tiangalo.lab.library.application.book.port.out.BookRepositoryPort;
import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookCategory;
import com.tiangalo.lab.library.domain.book.model.BookId;
import com.tiangalo.lab.library.domain.book.model.BookStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaBookRepositoryAdapterIntegrationTest.TestBeans.class)
class JpaBookRepositoryAdapterIntegrationTest {

    @Autowired
    private BookRepositoryPort repositoryPort;
    private Instant now;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        this.now = Instant.parse("2026-08-07T00:09:00Z");
        this.fixedClock = Clock.fixed(Instant.parse("2026-08-07T00:09:00Z"), ZoneOffset.UTC);
    }

    @Test
    void shouldPersistBook() {
        Book book = cleanCodeBook();
        repositoryPort.save(book);
        Optional<Book> foundBook = repositoryPort.findById(book.getId());
        assertThat(foundBook).isPresent();
        Book persistedBook = foundBook.get();
        assertThat(persistedBook.getId()).isEqualTo(book.getId());
        assertThat(persistedBook.getTitle()).isEqualTo("Clean Code");
        assertThat(persistedBook.getAuthor()).isEqualTo("Uncle Bob");
        assertThat(persistedBook.getIsbn()).isEqualTo("239239201");
        assertThat(persistedBook.getCategory()).isEqualTo(BookCategory.SOFTWARE_ENGINEERING);
        assertThat(persistedBook.getPublicationYear()).isEqualTo(2019);
        assertThat(persistedBook.getStatus()).isEqualTo(BookStatus.ACTIVE);
    }

    @Test
    void findByIdWhenBookDoesNotExistShouldReturnEmpty() {
        BookId bookId = BookId.newId();
        Optional<Book> foundBook = repositoryPort.findById(bookId);
        assertThat(foundBook).isEmpty();
    }

    @Test
    void existsByIsbnWhenBookExistsShouldReturnTrue() {
        Book book = cleanCodeBook();
        repositoryPort.save(book);
        boolean exists = repositoryPort.existsByIsbn(book.getIsbn());
        assertThat(exists).isTrue();
    }

    @Test
    void existsByIsbnWhenBookDoesNotExistShouldReturnFalse() {
        boolean exists = repositoryPort.existsByIsbn("123");
        assertThat(exists).isFalse();
    }

    @Test
    void existsByIsbnAndIdNotWhenAnotherBookHasSameIsbnShouldReturnTrue() {
        Book book = cleanCodeBook();
        repositoryPort.save(book);
        BookId bookId = BookId.newId();
        boolean exists = repositoryPort.existsByIsbnAndIdNot("239239201", bookId);
        assertThat(exists).isTrue();
    }

    @Test
    void existsByIsbnAndIdNotWhenSameBookHasIsbnShouldReturnFalse() {
        Book book = cleanCodeBook();
        repositoryPort.save(book);
        boolean exists = repositoryPort.existsByIsbnAndIdNot("239239201", book.getId());
        assertThat(exists).isFalse();
    }

    @Test
    void saveShouldUpdateExistingBook() {
        Book book = cleanCodeBook();
        repositoryPort.save(book);
        book.updateDetails(
                "Clean Architecture",
                "Tio Bob",
                "934910111",
                BookCategory.COMPUTER_SCIENCE,
                2018,
                now,
                currentYear());
        repositoryPort.save(book);
        Optional<Book> foundBook = repositoryPort.findById(book.getId());
        assertThat(foundBook).isPresent();
        Book persistedBook = foundBook.get();
        assertThat(persistedBook.getId()).isEqualTo(book.getId());
        assertThat(persistedBook.getTitle()).isEqualTo("Clean Architecture");
        assertThat(persistedBook.getAuthor()).isEqualTo("Tio Bob");
        assertThat(persistedBook.getIsbn()).isEqualTo("934910111");
        assertThat(persistedBook.getCategory()).isEqualTo(BookCategory.COMPUTER_SCIENCE);
        assertThat(persistedBook.getPublicationYear()).isEqualTo(2018);
        assertThat(persistedBook.getStatus()).isEqualTo(BookStatus.ACTIVE);
    }

    @Test
    void saveShouldPersistInactiveStatus() {
        Book book = cleanCodeBook();
        repositoryPort.save(book);
        book.deactivate(now);
        repositoryPort.save(book);
        Optional<Book> foundBook = repositoryPort.findById(book.getId());
        assertThat(foundBook).isPresent();
        Book persistedBook = foundBook.get();
        assertThat(persistedBook.getStatus()).isEqualTo(BookStatus.INACTIVE);
        assertThat(persistedBook.isActive()).isFalse();
    }

    @Test
    void searchShouldFilterByStatusActive() {
        Book firstBook = cleanCodeBook();
        repositoryPort.save(firstBook);
        Book secondBook = cleanArchitectureBook();
        secondBook.deactivate(now);
        repositoryPort.save(secondBook);
        List<Book> books = repositoryPort.search(new SearchBooksQuery(null, null, null, BookStatus.ACTIVE));
        assertThat(books).hasSize(1);
        assertThat(books).allMatch(Book::isActive);
    }

    @Test
    void searchShouldFilterByTitle() {
        Book firstBook = cleanCodeBook();
        repositoryPort.save(firstBook);
        Book secondBook = cleanArchitectureBook();
        repositoryPort.save(secondBook);
        List<Book> books = repositoryPort.search(new SearchBooksQuery("clean", null, null, null));
        assertThat(books).hasSize(2);
        assertThat(books).allMatch(book -> book.getTitle().toLowerCase(Locale.ROOT).contains("clean"));
    }

    @Test
    void searchShouldFilterByAuthor() {
        Book firstBook = cleanCodeBook();
        repositoryPort.save(firstBook);
        Book secondBook = cleanArchitectureBook();
        repositoryPort.save(secondBook);
        List<Book> books = repositoryPort.search(new SearchBooksQuery(null, "uncle", null, null));
        assertThat(books).hasSize(1);
        assertThat(books).allMatch(book -> book.getAuthor().toLowerCase(Locale.ROOT).contains("uncle"));
    }

    @Test
    void searchShouldFilterByCategory() {
        Book firstBook = cleanCodeBook();
        repositoryPort.save(firstBook);
        Book secondBook = cleanArchitectureBook();
        repositoryPort.save(secondBook);
        List<Book> books = repositoryPort.search(new SearchBooksQuery(null, null, BookCategory.COMPUTER_SCIENCE, null));
        assertThat(books).hasSize(1);
        assertThat(books).allMatch(book -> book.getCategory() == BookCategory.COMPUTER_SCIENCE);
    }

    @Test
    void searchShouldFilterByStatusInactive() {
        Book firstBook = cleanCodeBook();
        repositoryPort.save(firstBook);
        Book secondBook = cleanArchitectureBook();
        secondBook.deactivate(now);
        repositoryPort.save(secondBook);
        List<Book> books = repositoryPort.search(new SearchBooksQuery(null, null, null, BookStatus.INACTIVE));
        assertThat(books).hasSize(1);
        assertThat(books).allMatch(book -> !book.isActive());
    }

    private Integer currentYear() {
        return Year.now(fixedClock).getValue();
    }

    private Book cleanCodeBook() {
        return Book.create(
                "Clean Code",
                "Uncle Bob",
                "239239201",
                BookCategory.SOFTWARE_ENGINEERING,
                2019,
                now,
                currentYear());
    }

    private Book cleanArchitectureBook() {
        return Book.create(
                "Clean Architecture",
                "Tio Bob",
                "981239011",
                BookCategory.COMPUTER_SCIENCE,
                2018,
                now,
                currentYear());
    }

    @TestConfiguration
    static class TestBeans {

        @Bean
        Clock clock() {
            return Clock.fixed(
                    Instant.parse("2026-08-07T00:09:00Z"),
                    ZoneOffset.UTC
            );
        }

        @Bean
        BookPersistenceMapper bookPersistenceMapper(Clock clock) {
            return new BookPersistenceMapper(clock);
        }

        @Bean
        BookRepositoryPort bookRepositoryPort(
                SpringDataBookRepository springDataBookRepository,
                BookPersistenceMapper mapper
        ) {
            return new JpaBookRepositoryAdapter(springDataBookRepository, mapper);
        }
    }
}