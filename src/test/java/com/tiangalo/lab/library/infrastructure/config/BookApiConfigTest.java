package com.tiangalo.lab.library.infrastructure.config;

import com.tiangalo.lab.library.infrastructure.book.api.BookApiMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

class BookApiConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(BookApiConfig.class);

    @Test
    void bookApiConfigShouldCreateBookApiMapperBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(BookApiMapper.class);
            BookApiMapper mapper = context.getBean(BookApiMapper.class);
            assertThat(mapper).isNotNull();
        });
    }
}