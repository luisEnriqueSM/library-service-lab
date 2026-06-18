package com.tiangalo.lab.library.infrastructure.book.api;

import com.tiangalo.lab.library.application.book.command.CreateBookCommand;
import com.tiangalo.lab.library.application.book.port.in.CreateBookUseCase;
import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.infrastructure.book.api.request.CreateBookRequest;
import com.tiangalo.lab.library.infrastructure.book.api.response.BookResponse;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/books")
@ConditionalOnBean(CreateBookUseCase.class)
public class BookController {

    private final CreateBookUseCase createBookUseCase;
    private final BookApiMapper mapper;

    public BookController(
            CreateBookUseCase createBookUseCase,
            BookApiMapper mapper) {
        this.createBookUseCase = Objects.requireNonNull(createBookUseCase, "createBookUseCase cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "mapper cannot be null");
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) {
        CreateBookCommand command = mapper.toCreateCommand(request);
        Book book = createBookUseCase.createBook(command);
        BookResponse response = mapper.toResponse(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}