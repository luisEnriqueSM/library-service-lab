package com.tiangalo.lab.library.infrastructure.config;

import com.tiangalo.lab.library.infrastructure.book.api.BookApiMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookApiConfig {

    @Bean
    BookApiMapper bookApiMapper() {
        return new BookApiMapper();
    }
}