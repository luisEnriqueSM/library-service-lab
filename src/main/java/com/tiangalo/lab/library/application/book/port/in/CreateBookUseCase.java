package com.tiangalo.lab.library.application.book.port.in;

import com.tiangalo.lab.library.application.book.command.CreateBookCommand;
import com.tiangalo.lab.library.domain.book.model.Book;

public interface CreateBookUseCase {
    Book createBook(CreateBookCommand command);
}