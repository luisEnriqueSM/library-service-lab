package com.tiangalo.lab.library.application.book.port.in;

import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookId;

public interface DeactivateBookUseCase {
    Book deactivateBook(BookId bookId);
}