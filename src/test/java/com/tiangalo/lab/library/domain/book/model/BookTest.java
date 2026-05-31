package com.tiangalo.lab.library.domain.book.model;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.tiangalo.lab.library.domain.book.exception.InvalidBookException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookTest {

    @Test
    void createShouldReturnActiveBook() {
        Instant now = Instant.parse("2026-05-04T23:00:00Z");
        int currentYear = 2026;

        Book book = Book.create(
                "Clean Architecture",
                "Robert C Martin",
                "9780134494166",
                BookCategory.SOFTWARE_ENGINEERING,
                2017,
                now,
                currentYear);

        assertThat(book.getId()).isNotNull();
        assertThat(book.getTitle()).isEqualTo("Clean Architecture");
        assertThat(book.getAuthor()).isEqualTo("Robert C Martin");
        assertThat(book.getIsbn()).isEqualTo("9780134494166");
        assertThat(book.getCategory()).isEqualTo(BookCategory.SOFTWARE_ENGINEERING);
        assertThat(book.getStatus()).isEqualTo(BookStatus.ACTIVE);
        assertThat(book.getPublicationYear()).isEqualTo(2017);
        assertThat(book.getCreatedAt()).isEqualTo(now);
        assertThat(book.getUpdatedAt()).isEqualTo(now);
        assertThat(book.isActive()).isTrue();
    }

    @Test
    void createShouldRejectBlankTitle() {
        Instant now = Instant.parse("2026-05-04T23:00:00Z");
        int currentYear = 2026;
        assertThatThrownBy(() -> Book.create(
                " ",
                "Robert C Martin",
                "9780134494166",
                BookCategory.SOFTWARE_ENGINEERING,
                2017,
                now,
                currentYear))
                .isInstanceOf(InvalidBookException.class)
                .hasMessage("title is required");
    }

    @Test
    void createShouldRejectBlankAuthor() {
        Instant now = Instant.parse("2026-05-04T23:00:00Z");
        int currentYear = 2026;
        assertThatThrownBy(() -> Book.create(
                "Clean Architecture",
                " ",
                "9780134494166",
                BookCategory.SOFTWARE_ENGINEERING,
                2017,
                now,
                currentYear))
                .isInstanceOf(InvalidBookException.class)
                .hasMessage("author is required");
    }

    @Test
    void createShouldRejectBlankIsbn() {
        Instant now = Instant.parse("2026-05-04T23:00:00Z");
        int currentYear = 2026;
        assertThatThrownBy(() -> Book.create(
                "Clean Architecture",
                "Robert C Martin",
                " ",
                BookCategory.SOFTWARE_ENGINEERING,
                2017,
                now,
                currentYear))
                .isInstanceOf(InvalidBookException.class)
                .hasMessage("isbn is required");
    }

    @Test
    void createShouldRejectNullCategory() {
        Instant now = Instant.parse("2026-05-04T23:00:00Z");
        int currentYear = 2026;
        assertThatThrownBy(() -> Book.create(
                "Clean Architecture",
                "Robert C Martin",
                "9780134494166",
                null,
                2017,
                now,
                currentYear))
                .isInstanceOf(InvalidBookException.class)
                .hasMessage("category is required");
    }

    @Test
    void createShouldRejectNullPublicationYear() {
        Instant now = Instant.parse("2026-05-04T23:00:00Z");
        int currentYear = 2026;
        assertThatThrownBy(() -> Book.create(
                "Clean Architecture",
                "Robert C Martin",
                "9780134494166",
                BookCategory.SOFTWARE_ENGINEERING,
                null,
                now,
                currentYear))
                .isInstanceOf(InvalidBookException.class)
                .hasMessage("publicationYear is required");
    }

    @Test
    void createShouldRejectNullNow() {
        int currentYear = 2026;
        assertThatThrownBy(() -> Book.create(
                "Clean Architecture",
                "Robert C Martin",
                "9780134494166",
                BookCategory.SOFTWARE_ENGINEERING,
                2026,
                null,
                currentYear))
                .isInstanceOf(InvalidBookException.class)
                .hasMessage("now is required");
    }

    @Test
    void createShouldRejectFuturePublicationYear() {
        Instant now = Instant.parse("2026-05-04T23:00:00Z");
        int currentYear = 2026;
        assertThatThrownBy(() -> Book.create(
                "Clean Architecture",
                "Robert C Martin",
                "9780134494166",
                BookCategory.SOFTWARE_ENGINEERING,
                2027,
                now,
                currentYear))
                .isInstanceOf(InvalidBookException.class)
                .hasMessage("publicationYear must be equal or less than 2026");
    }

    @Test
    void restoreShouldPreserveExistingBookData() {
        BookId existingBookId = BookId.newId();
        Instant createdAt = Instant.parse("2026-05-04T23:00:00Z");
        Instant updatedAt = Instant.parse("2026-05-31T17:49:00Z");
        int currentYear = 2026;

        Book book = Book.restore(
                existingBookId,
                "Clean Code",
                "Robert C Martin",
                "9780134494166",
                BookCategory.SOFTWARE_ENGINEERING,
                2016,
                BookStatus.INACTIVE,
                createdAt,
                updatedAt,
                currentYear);
        assertThat(book.getId()).isEqualTo(existingBookId);
        assertThat(book.getTitle()).isEqualTo("Clean Code");
        assertThat(book.getAuthor()).isEqualTo("Robert C Martin");
        assertThat(book.getIsbn()).isEqualTo("9780134494166");
        assertThat(book.getCategory()).isEqualTo(BookCategory.SOFTWARE_ENGINEERING);
        assertThat(book.getPublicationYear()).isEqualTo(2016);
        assertThat(book.getCreatedAt()).isEqualTo(createdAt);
        assertThat(book.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(book.getStatus()).isEqualTo(BookStatus.INACTIVE);
        assertThat(book.isActive()).isFalse();
    }

    @Test
    void updateDetailsShouldUpdateEditableFieldsOnly() {
        Instant createdAt = Instant.parse("2026-05-04T23:00:00Z");
        int currentYear = 2026;
        Book book = Book.create(
                "Clean Architecture",
                "Robert C Martin",
                "9780134494166",
                BookCategory.SOFTWARE_ENGINEERING,
                2016,
                createdAt,
                currentYear);

        BookId originalBookId = book.getId();
        BookStatus originalBookStatus = book.getStatus();
        Instant originalCreatedAt = book.getCreatedAt();
        Instant updatedAt = Instant.parse("2026-05-31T16:46:00Z");

        book.updateDetails(
                "Clean Code",
                "Robert Martin",
                "9780134494167",
                BookCategory.COMPUTER_SCIENCE,
                2017,
                updatedAt,
                currentYear);
        assertThat(book.getId()).isEqualTo(originalBookId);
        assertThat(book.getStatus()).isEqualTo(originalBookStatus);
        assertThat(book.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(book.getTitle()).isEqualTo("Clean Code");
        assertThat(book.getAuthor()).isEqualTo("Robert Martin");
        assertThat(book.getIsbn()).isEqualTo("9780134494167");
        assertThat(book.getCategory()).isEqualTo(BookCategory.COMPUTER_SCIENCE);
        assertThat(book.getPublicationYear()).isEqualTo(2017);
        assertThat(book.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void updateDetailsShouldRejectFuturePublicationYear() {
        Instant now = Instant.parse("2026-05-04T23:00:00Z");
        int currentYear = 2026;

        Book book = Book.create(
                "Clean Architecture",
                "Robert C Martin",
                "9780134494166",
                BookCategory.SOFTWARE_ENGINEERING,
                2017,
                now,
                currentYear);
        assertThatThrownBy(() -> book.updateDetails(
                "Clean Code",
                "Robert C Martin",
                "9780134494167",
                BookCategory.COMPUTER_SCIENCE,
                2036,
                now,
                currentYear))
                .isInstanceOf(InvalidBookException.class)
                .hasMessage("publicationYear must be equal or less than 2026");
    }

    @Test
    void deactivateShouldMarkBookAsInactive() {
        Instant now = Instant.parse("2026-05-04T23:00:00Z");
        int currentYear = 2026;
        Book book = Book.create(
                "Clean Architecture",
                "Robert C Martin",
                "9780134494167",
                BookCategory.SOFTWARE_ENGINEERING,
                2017,
                now,
                currentYear);

        Instant updatedAt = Instant.parse("2026-05-29T23:00:00Z");
        book.deactivate(updatedAt);

        assertThat(book.isActive()).isFalse();
        assertThat(book.getStatus()).isEqualTo(BookStatus.INACTIVE);
        assertThat(book.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void deactivateShouldRejectNullUpdatedAt() {
        Instant now = Instant.parse("2026-05-04T23:00:00Z");
        int currentYear = 2026;

        Book book = Book.create(
                "Clean Architecture",
                "Robert C Martin",
                "9780134494166",
                BookCategory.SOFTWARE_ENGINEERING,
                2017,
                now,
                currentYear);
        assertThatThrownBy(() -> book.deactivate(null))
                .isInstanceOf(InvalidBookException.class)
                .hasMessage("updatedAt is required");
    } 
}