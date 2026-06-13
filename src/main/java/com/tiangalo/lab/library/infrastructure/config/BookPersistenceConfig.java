package com.tiangalo.lab.library.infrastructure.config;

import com.tiangalo.lab.library.application.book.port.out.BookRepositoryPort;
import com.tiangalo.lab.library.infrastructure.book.persistence.BookPersistenceMapper;
import com.tiangalo.lab.library.infrastructure.book.persistence.JpaBookRepositoryAdapter;
import com.tiangalo.lab.library.infrastructure.book.persistence.SpringDataBookRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import java.time.Clock;

@Configuration
public class BookPersistenceConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    BookPersistenceMapper bookPersistenceMapper(Clock clock) {
        return new BookPersistenceMapper(clock);
    }

    @Bean
    @ConditionalOnBean(SpringDataBookRepository.class)
    BookRepositoryPort bookRepositoryPort(
            SpringDataBookRepository repository,
            BookPersistenceMapper mapper
    ) {
        return new JpaBookRepositoryAdapter(repository, mapper);
    }
}