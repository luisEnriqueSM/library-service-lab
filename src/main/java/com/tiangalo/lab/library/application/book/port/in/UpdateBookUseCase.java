package com.tiangalo.lab.library.application.book.port.in;

import com.tiangalo.lab.library.application.book.command.UpdateBookCommand;
import com.tiangalo.lab.library.domain.book.model.Book;

public interface UpdateBookUseCase {
    Book updateBook(UpdateBookCommand command);
}