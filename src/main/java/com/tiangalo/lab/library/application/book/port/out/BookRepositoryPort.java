package com.tiangalo.lab.library.application.book.port.out;

import java.util.List;
import java.util.Optional;

import com.tiangalo.lab.library.application.book.command.SearchBooksQuery;
import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookId;

public interface BookRepositoryPort {
    Book save(Book book);
    Optional<Book> findById(BookId bookId);
    List<Book> search(SearchBooksQuery query);
    boolean existsByIsbn(String isbn);
    boolean existsByIsbnAndIdNot(String isbn, BookId bookId);
}