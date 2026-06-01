package com.tiangalo.lab.library.application.book.exception;

public class DuplicatedIsbnException extends RuntimeException {

    public DuplicatedIsbnException(String message) {
        super(message);
    }
}
