package com.tiangalo.lab.library.infrastructure.config;

import com.tiangalo.lab.library.application.book.port.out.BookRepositoryPort;
import com.tiangalo.lab.library.application.book.service.BookApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThat;

class BookApplicationConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(BookApplicationConfig.class)
            .withBean(BookRepositoryPort.class, () -> mock(BookRepositoryPort.class))
            .withBean(Clock.class, () -> Clock.fixed(Instant.parse("2026-06-17T23:48:00Z"), ZoneOffset.UTC));

    @Test
    void bookApplicationConfigShouldCreateBookApplicationServiceBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(BookApplicationService.class);
            BookApplicationService service = context.getBean(BookApplicationService.class);
            assertThat(service).isNotNull();
        });
    }
}