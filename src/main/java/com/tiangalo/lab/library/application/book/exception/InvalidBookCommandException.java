package com.tiangalo.lab.library.application.book.exception;

public class InvalidBookCommandException extends RuntimeException{
    public InvalidBookCommandException(String message) {
        super(message);
    }
}
