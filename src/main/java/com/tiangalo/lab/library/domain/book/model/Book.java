package com.tiangalo.lab.library.domain.book.model;

import java.time.Instant;

import com.tiangalo.lab.library.domain.book.exception.InvalidBookException;

public class Book {

    private final BookId id;
    private String title;
    private String author;
    private String isbn;
    private BookCategory category;
    private Integer publicationYear;
    private BookStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Book(
            BookId id,
            String title,
            String author,
            String isbn,
            BookCategory category,
            Integer publicationYear,
            BookStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publicationYear = publicationYear;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Book create(
            String title,
            String author,
            String isbn,
            BookCategory category,
            Integer publicationYear,
            Instant now,
            Integer currentYear) {
        validateRequiredText(title, "title");
        validateRequiredText(author, "author");
        validateRequiredText(isbn, "isbn");
        requireNonNull(category, "category");
        requireNonNull(publicationYear, "publicationYear");
        requireNonNull(now, "now");
        requireNonNull(currentYear, "currentYear");
        validatePublicationYear(publicationYear, currentYear);
        return new Book(
                BookId.newId(),
                title,
                author,
                isbn,
                category,
                publicationYear,
                BookStatus.ACTIVE,
                now,
                now);
    }

    public static Book restore(
            BookId id,
            String title,
            String author,
            String isbn,
            BookCategory category,
            Integer publicationYear,
            BookStatus status,
            Instant createdAt,
            Instant updatedAt,
            Integer currentYear) {
        requireNonNull(id, "id");
        validateRequiredText(title, "title");
        validateRequiredText(author, "author");
        validateRequiredText(isbn, "isbn");
        requireNonNull(category, "category");
        requireNonNull(publicationYear, "publicationYear");
        requireNonNull(status, "status");
        requireNonNull(createdAt, "createdAt");
        requireNonNull(updatedAt, "updatedAt");
        requireNonNull(currentYear, "currentYear");
        validatePublicationYear(publicationYear, currentYear);
        return new Book(
                id,
                title,
                author,
                isbn,
                category,
                publicationYear,
                status,
                createdAt,
                updatedAt);
    }

    public void updateDetails(
            String title,
            String author,
            String isbn,
            BookCategory category,
            Integer publicationYear,
            Instant updatedAt,
            Integer currentYear) {
        validateRequiredText(title, "title");
        validateRequiredText(author, "author");
        validateRequiredText(isbn, "isbn");
        requireNonNull(category, "category");
        requireNonNull(publicationYear, "publicationYear");
        requireNonNull(updatedAt, "updatedAt");
        requireNonNull(currentYear, "currentYear");
        validatePublicationYear(publicationYear, currentYear);
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publicationYear = publicationYear;
        this.updatedAt = updatedAt;
    }

    public void deactivate(
            Instant updatedAt) {
        requireNonNull(updatedAt, "updatedAt");
        this.status = BookStatus.INACTIVE;
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return this.status == BookStatus.ACTIVE;
    }

    private static void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidBookException(fieldName + " is required");
        }
    }

    private static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new InvalidBookException(fieldName + " is required");
        }
    }

    private static void validatePublicationYear(Integer publicationYear, Integer currentYear) {
        if (publicationYear > currentYear) {
            throw new InvalidBookException("publicationYear must be equal or less than " + currentYear);
        }
    }

    public BookId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public BookCategory getCategory() {
        return category;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public BookStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
