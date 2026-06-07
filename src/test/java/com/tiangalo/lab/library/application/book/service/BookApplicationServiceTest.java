package com.tiangalo.lab.library.application.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tiangalo.lab.library.application.book.command.CreateBookCommand;
import com.tiangalo.lab.library.application.book.command.SearchBooksQuery;
import com.tiangalo.lab.library.application.book.command.UpdateBookCommand;
import com.tiangalo.lab.library.application.book.exception.BookNotFoundException;
import com.tiangalo.lab.library.application.book.exception.DuplicatedIsbnException;
import com.tiangalo.lab.library.application.book.exception.InvalidBookCommandException;
import com.tiangalo.lab.library.application.book.port.out.BookRepositoryPort;
import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookCategory;
import com.tiangalo.lab.library.domain.book.model.BookId;
import com.tiangalo.lab.library.domain.book.model.BookStatus;

class BookApplicationServiceTest {

    private BookRepositoryPort repository;
    private BookApplicationService service;
    private BookApplicationService updateService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-05-04T23:00:00Z"), ZoneOffset.UTC);
        Clock updateClock = Clock.fixed(Instant.parse("2026-06-07T23:00:00Z"), ZoneOffset.UTC);
        this.repository = new FakeBookRepository();
        this.service = new BookApplicationService(repository, fixedClock);
        this.updateService = new BookApplicationService(repository, updateClock);
    }

    @Test
    void createBookShouldSaveValidBook() {
        CreateBookCommand command = createCleanArchitectureCommand();
        Book book = service.createBook(command);
        Instant expectedNow = Instant.parse("2026-05-04T23:00:00Z");
        assertThat(book).isNotNull();
        assertThat(book.getStatus()).isEqualTo(BookStatus.ACTIVE);
        assertThat(book.getCreatedAt()).isEqualTo(expectedNow);
        assertThat(book.getUpdatedAt()).isEqualTo(expectedNow);

        Book bookRepository = repository.findById(book.getId())
                .orElseThrow(() -> new BookNotFoundException("book not found"));
        assertThat(bookRepository).isNotNull();
        assertThat(bookRepository.getId()).isNotNull();
        assertThat(bookRepository.getTitle()).isEqualTo(command.title());
        assertThat(bookRepository.getAuthor()).isEqualTo(command.author());
        assertThat(bookRepository.getIsbn()).isEqualTo(command.isbn());
        assertThat(bookRepository.getCategory()).isEqualTo(command.category());
        assertThat(bookRepository.getPublicationYear()).isEqualTo(command.publicationYear());
    }

    @Test
    void createBookShouldRejectDuplicateIsbn() {
        CreateBookCommand command = createCleanArchitectureCommand();
        assertThat(service.createBook(command)).isNotNull();
        assertThatThrownBy(() -> service.createBook(command))
                .isInstanceOf(DuplicatedIsbnException.class)
                .hasMessage("isbn: " + command.isbn() + " already exists");
    }

    @Test
    void createBookShouldRejectNullCommand() {
        assertThatThrownBy(() -> service.createBook(null))
                .isInstanceOf(InvalidBookCommandException.class)
                .hasMessage("command is required");
    }

    @Test
    void getBookByIdShouldReturnExistingBook() {
        CreateBookCommand command = createCleanArchitectureCommand();
        Book book = service.createBook(command);
        assertThat(book).isNotNull();
        Book bookFound = service.getBookById(book.getId());
        assertThat(bookFound).isNotNull();
        assertThat(bookFound.getId()).isEqualTo(book.getId());
        assertThat(bookFound.getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    void getBookByIdShouldRejectMissingBook() {
        assertThatThrownBy(() -> service.getBookById(BookId.newId()))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found");
    }

    @Test
    void getBookByIdShouldRejectNullBookId() {
        assertThatThrownBy(() -> service.getBookById(null))
                .isInstanceOf(InvalidBookCommandException.class)
                .hasMessage("bookId is required");
    }

    @Test
    void searchBooksShouldUseActiveStatusWhenQueryNull() {
        CreateBookCommand firstBookCommand = createCleanArchitectureCommand();
        CreateBookCommand secondBookCommand = createCleanCodeCommand();
        service.createBook(firstBookCommand);
        Book secondBook = service.createBook(secondBookCommand);
        service.deactivateBook(secondBook.getId());
        List<Book> books = service.searchBooks(null);
        assertThat(books).hasSize(1);
        assertThat(books).allMatch(Book::isActive);
    }

    @Test
    void searchBooksShouldUseActiveStatusWhenStatusIsNull() {
        CreateBookCommand firstBookCommand = createCleanArchitectureCommand();
        CreateBookCommand secondBookCommand = createCleanCodeCommand();
        service.createBook(firstBookCommand);
        Book secondBook = service.createBook(secondBookCommand);
        service.deactivateBook(secondBook.getId());
        List<Book> books = service.searchBooks(new SearchBooksQuery(null, null, null, null));
        assertThat(books).hasSize(1);
        assertThat(books).allMatch(Book::isActive);
    }

    @Test
    void searchBooksShouldKeepProvidedStatus() {
        CreateBookCommand firstBookCommand = createCleanArchitectureCommand();
        CreateBookCommand secondBookCommand = createCleanCodeCommand();
        service.createBook(firstBookCommand);
        Book secondBook = service.createBook(secondBookCommand);
        Instant now = Instant.parse("2026-05-04T23:00:00Z");
        secondBook.deactivate(now);
        List<Book> books = service.searchBooks(new SearchBooksQuery(null, null, null, BookStatus.INACTIVE));
        assertThat(books).hasSize(1);
        assertThat(books).allMatch(book -> !book.isActive());
    }

    @Test
    void updateBookShouldUpdateExistingBook() {
        CreateBookCommand command = createCleanArchitectureCommand();
        Book book = service.createBook(command);
        BookId originalBookId = book.getId();
        Instant originalCreatedAt = book.getCreatedAt();
        Instant originalUpdatedAt = book.getUpdatedAt();
        BookStatus originalBookStatus = book.getStatus();
        UpdateBookCommand updateBookCommand = new UpdateBookCommand(
                book.getId(),
                "Clean Code",
                "Uncle Bob",
                "9780134494167",
                BookCategory.ARTIFICIAL_INTELLIGENCE,
                2019);
        Book bookUpdated = updateService.updateBook(updateBookCommand);
        assertThat(bookUpdated.getId()).isEqualTo(originalBookId);
        assertThat(bookUpdated.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(bookUpdated.getTitle()).isEqualTo("Clean Code");
        assertThat(bookUpdated.getAuthor()).isEqualTo("Uncle Bob");
        assertThat(bookUpdated.getIsbn()).isEqualTo("9780134494167");
        assertThat(bookUpdated.getCategory()).isEqualTo(BookCategory.ARTIFICIAL_INTELLIGENCE);
        assertThat(bookUpdated.getStatus()).isEqualTo(originalBookStatus);
        assertThat(bookUpdated.getPublicationYear()).isEqualTo(2019);
        Instant updatedAt = Instant.parse("2026-06-07T23:00:00Z");
        assertThat(bookUpdated.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(bookUpdated.getUpdatedAt()).isNotEqualTo(originalUpdatedAt);
    }

    @Test
    void updateBookShouldRejectMissingBook() {
        UpdateBookCommand updateBookCommand = new UpdateBookCommand(
                BookId.newId(),
                "Clean Code",
                "Uncle Bob",
                "9780134494167",
                BookCategory.ARTIFICIAL_INTELLIGENCE,
                2019);
        assertThatThrownBy(() -> updateService.updateBook(updateBookCommand))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found");
    }

    @Test
    void updateBookShouldRejectDuplicatedIsbnFromAnotherBook() {
        CreateBookCommand firstBookCommand = createCleanArchitectureCommand();
        Book book = service.createBook(firstBookCommand);

        CreateBookCommand secondBookCommand = createCleanCodeCommand();
        service.createBook(secondBookCommand);

        UpdateBookCommand updateBookCommand = new UpdateBookCommand(
                book.getId(),
                "Learn Java Programming",
                "Uncle Bob",
                "9780134494167",
                BookCategory.ARTIFICIAL_INTELLIGENCE,
                2019);

        assertThatThrownBy(() -> service.updateBook(updateBookCommand))
                .isInstanceOf(DuplicatedIsbnException.class)
                .hasMessage("isbn: " + updateBookCommand.isbn() + " already exists");
    }

    @Test
    void updateBookShouldRejectNullCommand() {
        assertThatThrownBy(() -> service.updateBook(null))
                .isInstanceOf(InvalidBookCommandException.class)
                .hasMessage("command is required");
    }

    @Test
    void deactivateBookShouldDeactivateExistingBook() {
        CreateBookCommand firstBookCommand = createCleanArchitectureCommand();
        Book book = service.createBook(firstBookCommand);
        Instant originalUpdatedAt = book.getUpdatedAt();
        Book deactivatedBook = updateService.deactivateBook(book.getId());
        Instant updatedAt = Instant.parse("2026-06-07T23:00:00Z");
        assertThat(deactivatedBook.getStatus()).isEqualTo(BookStatus.INACTIVE);
        assertThat(deactivatedBook.isActive()).isFalse();
        assertThat(deactivatedBook.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(deactivatedBook.getUpdatedAt()).isNotEqualTo(originalUpdatedAt);
    }

    @Test
    void deactivateBookShouldRejectMissingBook() {
        assertThatThrownBy(() -> service.deactivateBook(BookId.newId()))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found");
    }

    @Test
    void deactivateBookShouldRejectNullBookId() {
        assertThatThrownBy(() -> service.deactivateBook(null))
                .isInstanceOf(InvalidBookCommandException.class)
                .hasMessage("bookId is required");
    }

    private CreateBookCommand createCleanArchitectureCommand() {
        return new CreateBookCommand(
                "Clean Architecture",
                "Robert C Martin",
                "9780134494166",
                BookCategory.SOFTWARE_ENGINEERING,
                2016);
    }

    private CreateBookCommand createCleanCodeCommand() {
        return new CreateBookCommand(
                "Clean Code",
                "Robert C Martin",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING,
                2016);
    }
}
