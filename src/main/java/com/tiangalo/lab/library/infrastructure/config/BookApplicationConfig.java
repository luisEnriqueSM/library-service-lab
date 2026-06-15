package com.tiangalo.lab.library.infrastructure.config;

import com.tiangalo.lab.library.application.book.port.out.BookRepositoryPort;
import com.tiangalo.lab.library.application.book.service.BookApplicationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BookApplicationConfig {

    @Bean
    @ConditionalOnBean(BookRepositoryPort.class)
    BookApplicationService bookApplicationService(
            BookRepositoryPort repository,
            Clock clock
    ) {
        return new BookApplicationService(repository, clock);
    }
}