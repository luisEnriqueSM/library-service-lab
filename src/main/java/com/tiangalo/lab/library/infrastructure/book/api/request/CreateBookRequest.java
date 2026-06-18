package com.tiangalo.lab.library.infrastructure.book.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBookRequest(
        @NotBlank String title,
        @NotBlank String author,
        @NotBlank String isbn,
        @NotBlank String category,
        @NotNull Integer publicationYear) {}