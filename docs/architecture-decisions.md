# Architecture Decisions

## ADR-001: Use a learning lab before building Tiangalo services

### Status
Accepted

### Context
Before building Tiangalo services, we need a controlled environment to practice Spring Boot, hexagonal architecture, persistence, testing, Docker, Kubernetes, and AWS deployment.

### Decision
We will first build a small `library-service` lab before applying the same patterns to Tiangalo.

### Consequences
The lab will reduce risk before applying the same patterns to Tiangalo services.

It will also allow us to practice architecture and implementation decisions in a smaller domain before moving into the real portfolio project.

---

## ADR-002: Build step by step

### Status
Accepted

### Context
The purpose of this lab is not only to build a working service, but to understand each architectural and technical decision behind it.

Building the whole service at once would make the learning process harder and could lead to copying code without understanding the design.

### Decision
The project will be developed incrementally. Each stage must be understood, implemented, tested, reviewed, and committed before moving to the next one.

### Consequences
Development will be slower at the beginning, but each step will be easier to understand, review, and improve.

This approach will also create a cleaner Git history and make the project easier to explain in interviews.

---

## ADR-003: Use Spring Boot 4 and Java 21

### Status
Accepted

### Context
The lab should use a modern and stable Java/Spring ecosystem to practice current backend development standards.

Java 21 is a Long-Term Support version and Spring Boot 4.x provides a modern baseline for building Spring applications.

### Decision
We will use Spring Boot 4.x and Java 21 for the lab.

The service will use Maven as the build tool and will initially include only the minimum dependencies required for a REST microservice with persistence capabilities.

### Consequences
The project will use a modern Java baseline while staying aligned with enterprise backend practices.

Developers working on the project will need Java 21 installed locally.

---

## ADR-004: Temporarily disable database auto-configuration

### Status
Superseded by ADR-008

### Context
Spring Data JPA is included from the beginning because the service will eventually use persistence. However, database configuration is not introduced in the first application setup stage.

When Spring Boot detects JPA in the classpath, it tries to auto-configure a `DataSource`, `EntityManagerFactory`, Hibernate, and connection pooling. Without database connection properties, the application fails to start.

### Decision
To keep the application bootstrappable while persistence is not configured, `DataSourceAutoConfiguration` and `HibernateJpaAutoConfiguration` are temporarily excluded.

This exclusion must be removed when database configuration is introduced.

### Consequences
The application can start without a database during the initial setup stage.

This is a temporary decision. When persistence is implemented, database configuration must be added and the exclusions must be removed.

---

## ADR-005: Start the lab with a Book-centered domain

### Status
Accepted

### Context
The lab needs a small but realistic domain to practice Spring Boot, REST contracts, validation, persistence, testing, and hexagonal architecture without introducing excessive business complexity.

### Decision
The first version of the service will focus on managing books.

The initial use cases are:

- create book
- get book by id
- search books
- update book
- deactivate book

### Consequences
The domain remains simple enough for learning while still allowing us to practice business rules, uniqueness constraints, lifecycle status, and API error handling.

## ADR-006: Use hexagonal architecture for the service

### Status
Accepted

### Context
The lab will use the hexagonal architecture to separate in layers. The goal is to keep the business logic independent from external technologies 

### Decision
This architecture will help us build a service that is easier to test, maintain, refactor, and evolve.

## ADR-007: Use JPA persistence adapter for library-service

### Status
Accepted

### Context
The application layer depends on BookRepositoryPort. We need a real persistence implementation without coupling domain or application to JPA.

### Decision
We will implement a persistence adapter using Spring Data JPA. Domain Book will remain persistence-agnostic. A separate BookJpaEntity will represent the database table, and BookPersistenceMapper will convert between Book and BookJpaEntity.

### Consequences
- Domain remains clean from JPA annotations.
- Infrastructure owns database-specific concerns.
- Mapping code is required between domain and persistence.
- Tests can validate persistence separately from domain and application.

---

## ADR-008: Enable local dev runtime profile and fail-fast wiring

### Status

Accepted

### Context

The service now includes the API, application, domain, and persistence layers.

Previously, database auto-configuration was temporarily disabled because persistence was not configured yet. This allowed the application to start during the initial setup stage.

Some production beans also used conditional registration with `@ConditionalOnBean`. This avoided startup failures while the project was still incomplete, but it also made missing critical wiring harder to detect.

During manual smoke testing, `POST /api/books` returned `404 Not Found` because the controller was not registered. Spring treated `/api/books` as a static resource instead of an API endpoint.

### Decision

We will enable a local `dev` runtime profile using H2 and JPA so the application can be started and tested end to end.

We will remove `DataSourceAutoConfiguration` and `HibernateJpaAutoConfiguration` exclusions from the main application class.

We will also remove `@ConditionalOnBean` from production code for critical components such as controllers, application services, and persistence adapters.

The application should fail fast during startup when required beans are missing instead of starting with incomplete functionality.

### Consequences

The application can now start locally with the `dev` profile and execute real HTTP requests against an in-memory H2 database.

Missing wiring problems are detected during startup instead of appearing later as missing endpoints or misleading HTTP errors.

This improves confidence in the real runtime behavior of the service beyond isolated unit and integration tests.

The `dev` profile is intended for local development only. Production-like persistence will be handled later with a dedicated database setup.

### Validation

The application was started using the `dev` profile.

The following endpoints were manually verified:

* `POST /api/books`
* `GET /api/books/{id}`
* `GET /api/books?status=ACTIVE`
* `PUT /api/books/{id}`
* `DELETE /api/books/{id}`
* `GET /api/books?status=INACTIVE`

The full test suite was executed successfully:

```bash
./mvnw test
```

Result:

```text
Tests run: 86, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
