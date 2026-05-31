package com.tiangalo.lab.library.application.book.port.in;

import java.util.List;

import com.tiangalo.lab.library.application.book.command.SearchBooksQuery;
import com.tiangalo.lab.library.domain.book.model.Book;

public interface SearchBooksUseCase {
    List<Book> searchBooks(SearchBooksQuery query);
}