package com.tiangalo.lab.library.infrastructure.book.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import com.tiangalo.lab.library.application.book.command.CreateBookCommand;
import com.tiangalo.lab.library.application.book.command.SearchBooksQuery;
import com.tiangalo.lab.library.application.book.command.UpdateBookCommand;
import com.tiangalo.lab.library.application.book.port.in.CreateBookUseCase;
import com.tiangalo.lab.library.application.book.port.in.GetBookByIdUseCase;
import com.tiangalo.lab.library.application.book.port.in.SearchBooksUseCase;
import com.tiangalo.lab.library.application.book.port.in.UpdateBookUseCase;
import com.tiangalo.lab.library.application.book.port.in.DeactivateBookUseCase;
import com.tiangalo.lab.library.domain.book.model.Book;
import com.tiangalo.lab.library.domain.book.model.BookCategory;
import com.tiangalo.lab.library.domain.book.model.BookId;
import com.tiangalo.lab.library.domain.book.model.BookStatus;
import com.tiangalo.lab.library.infrastructure.book.api.request.CreateBookRequest;
import com.tiangalo.lab.library.infrastructure.book.api.request.UpdateBookRequest;
import com.tiangalo.lab.library.infrastructure.book.api.response.BookResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final CreateBookUseCase createBookUseCase;
    private final GetBookByIdUseCase getBookByIdUseCase;
    private final SearchBooksUseCase searchBooksUseCase;
    private final UpdateBookUseCase updateBookUseCase;
    private final DeactivateBookUseCase deactivateBookUseCase;
    private final BookApiMapper mapper;

    public BookController(
            CreateBookUseCase createBookUseCase,
            GetBookByIdUseCase getBookByIdUseCase,
            SearchBooksUseCase searchBooksUseCase,
            UpdateBookUseCase updateBookUseCase,
            DeactivateBookUseCase deactivateBookUseCase,
            BookApiMapper mapper) {
        this.createBookUseCase = Objects.requireNonNull(createBookUseCase, "createBookUseCase cannot be null");
        this.getBookByIdUseCase = Objects.requireNonNull(getBookByIdUseCase, "getBookByIdUseCase cannot be null");
        this.searchBooksUseCase = Objects.requireNonNull(searchBooksUseCase, "searchBooksUseCase cannot be null");
        this.updateBookUseCase = Objects.requireNonNull(updateBookUseCase, "updateBookUseCase cannot be null");
        this.deactivateBookUseCase = Objects.requireNonNull(deactivateBookUseCase, "deactivateBookUseCase cannot be null");
        this.mapper = Objects.requireNonNull(mapper, "mapper cannot be null");
    }

    @Operation(
            summary = "Create a book",
            description = "Creates a new book and returns the created resource."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Book created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "ISBN already exists",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) {
        CreateBookCommand command = mapper.toCreateCommand(request);
        Book book = createBookUseCase.createBook(command);
        BookResponse response = mapper.toResponse(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get a book by id",
            description = "Returns a book by its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable UUID id) {
        BookId bookId = BookId.from(id);
        Book book = getBookByIdUseCase.getBookById(bookId);
        BookResponse response = mapper.toResponse(book);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search books",
            description = "Searches books using optional filters such as title, author, category, and status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Books found",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BookResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search filter",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status
    ) {
        SearchBooksQuery query = new SearchBooksQuery(
                title,
                author,
                category != null ? BookCategory.valueOf(category) : null,
                status != null ? BookStatus.valueOf(status) : null
        );
        List<BookResponse> books = searchBooksUseCase.searchBooks(query)
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(books);
    }

    @Operation(
            summary = "Update a book",
            description = "Updates an existing book by its unique identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "ISBN already exists",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBookRequest request) {
        BookId bookId = BookId.from(id);
        UpdateBookCommand command = mapper.toUpdateCommand(bookId, request);
        Book book = updateBookUseCase.updateBook(command);
        BookResponse response = mapper.toResponse(book);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Deactivate a book",
            description = "Deactivates a book by its unique identifier and returns the updated resource."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Book deactivated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Book not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<BookResponse> deleteBook(@PathVariable UUID id) {
        BookId bookId = BookId.from(id);
        Book book = deactivateBookUseCase.deactivateBook(bookId);
        BookResponse response = mapper.toResponse(book);
        return ResponseEntity.ok(response);
    }
}