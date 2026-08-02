package com.tiangalo.lab.library.infrastructure.config;

import com.tiangalo.lab.library.application.book.port.out.BookRepositoryPort;
import com.tiangalo.lab.library.infrastructure.book.persistence.SpringDataBookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThat;

class BookPersistenceConfigTest {

    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(BookPersistenceConfig.class)
            .withBean(SpringDataBookRepository.class, () -> mock(SpringDataBookRepository.class));

    @Test
    void bookPersistenceConfigShouldCreateBookRepositoryPortBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(BookRepositoryPort.class);
            BookRepositoryPort bean = context.getBean(BookRepositoryPort.class);
            assertThat(bean).isNotNull();
        });
    }
}