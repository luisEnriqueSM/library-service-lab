package com.tiangalo.lab.library.domain.book.exception;

public class InvalidBookException extends RuntimeException{

    public InvalidBookException(String message){
        super(message);
    }
}
