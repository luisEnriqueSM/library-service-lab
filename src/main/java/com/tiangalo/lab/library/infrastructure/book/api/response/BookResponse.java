package com.tiangalo.lab.library.infrastructure.book.api.response;

import java.time.Instant;
import java.util.UUID;

public record BookResponse(
        UUID id,
        String title,
        String author,
        String isbn,
        String category,
        Integer publicationYear,
        String status,
        Instant createdAt,
        Instant updatedAt) {}