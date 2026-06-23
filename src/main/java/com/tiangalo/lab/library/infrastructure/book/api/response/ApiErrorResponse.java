package com.tiangalo.lab.library.infrastructure.book.api.response;

import java.time.Instant;

public record ApiErrorResponse(
        String code,
        String message,
        Instant timestamp
) {
}