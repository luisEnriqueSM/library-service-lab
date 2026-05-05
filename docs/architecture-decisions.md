# Architecture Decisions

## ADR-001: Use a learning lab before building Tiangalo services

We will first build a smal `library-service` to practice Spring Boot, hexagonal architecture, persistence, testing, Docker, Kubernetes, and AWS deployment before applaying the same patterns to Tiangalo.

## ADR-002: Build step by step

The project will be developed incementally. Each stage must be understood, implemented, tested, and reviewed before moving to the next one.

## ADR-003: Use Spring Boot 4 and Java 21

We will use Spring Boot 4.x and Java 21 for the lab to practice with a modern Spring ecosystem while keeping a stable LTS Java baseline.

The serive will use Maven as the build tool and will initially incluse only the minimum dependencies requiered for a REST microservice with persistence capabilities.

## ADR-004: Temporarily disable database auto-configuration

Spring Data JPA is included from the begining because the service will eventually use persistence. However, database configuration is not introduced in the first application setup stage.

To keep the application bootstrappable while persistence is not configured. `DataSourceAutoConfiguration` and `HibernateJpaAutoConfiguration` are temporarily excluded.

This exclusion must be removed when database configuration is introduced.