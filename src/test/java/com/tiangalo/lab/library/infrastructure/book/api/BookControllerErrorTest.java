package com.tiangalo.lab.library.infrastructure.book.api;

import com.tiangalo.lab.library.application.book.command.CreateBookCommand;
import com.tiangalo.lab.library.application.book.exception.BookNotFoundException;
import com.tiangalo.lab.library.application.book.exception.DuplicatedIsbnException;
import com.tiangalo.lab.library.application.book.port.in.CreateBookUseCase;
import com.tiangalo.lab.library.application.book.port.in.DeactivateBookUseCase;
import com.tiangalo.lab.library.application.book.port.in.GetBookByIdUseCase;
import com.tiangalo.lab.library.application.book.port.in.SearchBooksUseCase;
import com.tiangalo.lab.library.application.book.port.in.UpdateBookUseCase;
import com.tiangalo.lab.library.domain.book.model.BookId;
import com.tiangalo.lab.library.infrastructure.config.BookApiConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import({
        BookExceptionHandler.class,
        BookApiConfig.class,
        BookControllerErrorTest.TestBeans.class
})
class BookControllerErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetBookByIdUseCase getBookByIdUseCase;

    @Autowired
    private CreateBookUseCase createBookUseCase;

    @Test
    void getBookByIdWhenBookDoesNotExistShouldReturnNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(getBookByIdUseCase.getBookById(BookId.from(id)))
                .thenThrow(new BookNotFoundException("Book not found"));

        mockMvc.perform(get("/api/books/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Book not found"))
                .andExpect(jsonPath("$.timestamp").value("2026-06-17T23:48:00Z"));
    }

    @Test
    void createBookWhenIsbnAlreadyExistsShouldReturnConflict() throws Exception {
        String jsonBody = "{\"title\": \"Clean Code\", \"author\": \"Robert C Martin\", \"isbn\": \"9780134494167\", \"category\": \"SOFTWARE_ENGINEERING\", \"publicationYear\": 2019}";
        when(createBookUseCase.createBook(any(CreateBookCommand.class)))
                .thenThrow(new DuplicatedIsbnException("Duplicated isbn"));
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("DUPLICATED_ISBN"))
                .andExpect(jsonPath("$.message").value("Duplicated isbn"))
                .andExpect(jsonPath("$.timestamp").value("2026-06-17T23:48:00Z"));
    }

    @Test
    void createBookWhenRequestIsInvalidShouldReturnValidationError() throws Exception {
        String jsonBody = "{\"title\": \"\", \"author\": \"Robert C Martin\", \"isbn\": \"9780134494167\", \"category\": \"SOFTWARE_ENGINEERING\", \"publicationYear\": 2019}";
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("title: must not be blank"))
                .andExpect(jsonPath("$.timestamp").value("2026-06-17T23:48:00Z"));
        verifyNoInteractions(createBookUseCase);
    }

    @TestConfiguration
    static class TestBeans {

        @Bean
        Clock clock() {
            return Clock.fixed(
                    Instant.parse("2026-06-17T23:48:00Z"),
                    ZoneOffset.UTC
            );
        }

        @Bean
        CreateBookUseCase createBookUseCase() {
            return mock(CreateBookUseCase.class);
        }

        @Bean
        GetBookByIdUseCase getBookByIdUseCase() {
            return mock(GetBookByIdUseCase.class);
        }

        @Bean
        SearchBooksUseCase searchBooksUseCase() {
            return mock(SearchBooksUseCase.class);
        }

        @Bean
        UpdateBookUseCase updateBookUseCase() {
            return mock(UpdateBookUseCase.class);
        }

        @Bean
        DeactivateBookUseCase deactivateBookUseCase() {
            return mock(DeactivateBookUseCase.class);
        }

        @Bean
        BookController bookController(
                CreateBookUseCase createBookUseCase,
                GetBookByIdUseCase getBookByIdUseCase,
                SearchBooksUseCase searchBooksUseCase,
                UpdateBookUseCase updateBookUseCase,
                DeactivateBookUseCase deactivateBookUseCase,
                BookApiMapper mapper
        ) {
            return new BookController(
                    createBookUseCase,
                    getBookByIdUseCase,
                    searchBooksUseCase,
                    updateBookUseCase,
                    deactivateBookUseCase,
                    mapper
            );
        }
    }
}