# Hexagonal Architecture

## Purpose

This document defines the hexagonal architecture structure used in the `library-service`.

The goal is to keep the business logic independent from external technologies such as REST, Spring MVC, JPA, MySQL, Kafka, Redis, or any other infrastructure detail.

This architecture will help us build a service that is easier to test, maintain, refactor, and evolve.

---

## Core idea

Hexagonal Architecture, also known as Ports and Adapters, organizes the application around the business domain.

The domain and application logic live at the center.

External mechanisms such as HTTP controllers, databases, message brokers, and external APIs live outside the core and communicate with it through ports.

```text
External world
    ↓
Adapters
    ↓
Ports
    ↓
Application
    ↓
Domain
```

The most important rule is:

> The business core should not depend on external technical details.

---

## Main layers

The service will be organized into four main areas:

```text
com.tiangalo.lab.library
├── domain
├── application
├── infrastructure
└── api
```

---

## Domain layer

The `domain` layer contains the core business concepts and rules.

For this service, the main domain concept is `Book`.

Examples:

```text
domain.book.model.Book
domain.book.model.BookId
domain.book.model.BookStatus
domain.book.model.BookCategory
```

The domain layer should contain:

- business models
- business rules
- value objects
- domain-specific validations
- domain exceptions, when needed

The domain layer must not depend on:

- Spring
- JPA
- REST
- JSON
- database entities
- external APIs

### Rule

The domain must be plain Java.

It should not use annotations such as:

```text
@RestController
@Service
@Repository
@Entity
@Table
@Autowired
```

---

## Application layer

The `application` layer contains the use cases of the service.

It coordinates the business flow but does not know technical implementation details.

Examples of use cases:

```text
CreateBookUseCase
GetBookByIdUseCase
SearchBooksUseCase
UpdateBookUseCase
DeactivateBookUseCase
```

The application layer can depend on:

- the domain layer
- its own ports

The application layer must not depend on:

- JPA repositories
- database entities
- REST controllers
- external clients
- infrastructure implementations

### Important rule

The application layer can depend on ports, but it must not know who implements those ports.

For example, the application can depend on:

```text
BookRepositoryPort
```

But it must not depend directly on:

```text
JpaBookRepositoryAdapter
SpringDataBookRepository
BookEntity
```

This allows the application logic to remain independent from MySQL, JPA, or any other persistence mechanism.

---

## Ports

Ports are interfaces that define how the application communicates with the outside world.

There are two main types of ports:

---

## Input ports

Input ports represent what the application can do.

They are use case contracts.

Examples:

```text
CreateBookUseCase
GetBookByIdUseCase
SearchBooksUseCase
UpdateBookUseCase
DeactivateBookUseCase
```

An input adapter, such as a REST controller, calls these input ports.

Example flow:

```text
BookController
    ↓
CreateBookUseCase
```

---

## Output ports

Output ports represent what the application needs from external systems.

For example, the application needs to persist and retrieve books, but it should not know if the data comes from MySQL, PostgreSQL, MongoDB, or an in-memory repository.

Example:

```text
BookRepositoryPort
```

This port can define operations such as:

```text
save book
find book by id
search books
check if ISBN exists
```

The application depends on this interface.

The infrastructure layer provides the implementation.

Example:

```text
JpaBookRepositoryAdapter implements BookRepositoryPort
```

---

## Infrastructure layer

The `infrastructure` layer contains technical implementations.

This layer can use frameworks and external technologies.

Examples:

```text
infrastructure.persistence.book.entity.BookEntity
infrastructure.persistence.book.repository.SpringDataBookRepository
infrastructure.persistence.book.adapter.JpaBookRepositoryAdapter
infrastructure.persistence.book.mapper.BookPersistenceMapper
```

The infrastructure layer may depend on:

- Spring
- JPA
- MySQL
- external APIs
- file systems
- message brokers

The infrastructure layer implements output ports defined by the application layer.

Example:

```text
BookRepositoryPort
    ↑
JpaBookRepositoryAdapter
    ↓
SpringDataBookRepository
    ↓
MySQL
```

---

## API layer

The `api` layer contains HTTP-related components.

Examples:

```text
api.book.controller.BookController
api.book.dto.CreateBookRequest
api.book.dto.UpdateBookRequest
api.book.dto.BookResponse
api.common.advice.GlobalExceptionHandler
```

The API layer is responsible for:

- receiving HTTP requests
- validating request format
- mapping HTTP DTOs to application commands
- calling input ports
- mapping application results to HTTP responses
- returning the correct HTTP status codes

The API layer must not contain business logic.

---

## Package structure

The initial package structure will be:

```text
com.tiangalo.lab.library
├── LibraryServiceApplication.java
├── domain
│   └── book
│       ├── model
│       └── exception
├── application
│   └── book
│       ├── port
│       │   ├── in
│       │   └── out
│       ├── command
│       └── service
├── infrastructure
│   └── persistence
│       └── book
│           ├── entity
│           ├── mapper
│           ├── repository
│           └── adapter
└── api
    ├── book
    │   ├── controller
    │   └── dto
    └── common
        └── advice
```

---

## Dependency rules

Dependencies must point inward.

Allowed dependencies:

```text
api → application
api → domain

application → domain

infrastructure → application
infrastructure → domain
```

Not allowed dependencies:

```text
domain → application
domain → infrastructure
domain → api

application → infrastructure
application → api

api → infrastructure
```

The application layer should not know which adapter implements its ports.

The infrastructure layer knows about the application ports because it implements them.

---

## Example: create book flow

The `Create book` use case follows this flow:

```text
HTTP Request
    ↓
BookController
    ↓
CreateBookRequest
    ↓
CreateBookCommand
    ↓
CreateBookUseCase
    ↓
BookApplicationService
    ↓
Book domain model
    ↓
BookRepositoryPort
    ↓
JpaBookRepositoryAdapter
    ↓
SpringDataBookRepository
    ↓
MySQL
```

Response flow:

```text
MySQL
    ↓
BookEntity
    ↓
BookPersistenceMapper
    ↓
Book domain model
    ↓
BookApplicationService
    ↓
BookResponse
    ↓
HTTP Response
```

---

## Why not expose JPA entities directly?

JPA entities are persistence details.

They should not be used as API responses or domain models.

Bad practice:

```text
Controller → Repository → BookEntity → HTTP Response
```

Problems:

- the API becomes coupled to the database model
- internal fields may be exposed accidentally
- database changes can break the public API
- business rules become harder to isolate
- tests become more coupled to infrastructure

Preferred approach:

```text
BookEntity ↔ Book ↔ BookResponse
```

Each model has a clear responsibility:

| Model | Layer | Purpose |
|---|---|---|
| Book | Domain | Business concept |
| BookEntity | Infrastructure | Database persistence model |
| BookResponse | API | HTTP response contract |
| CreateBookRequest | API | HTTP request contract |
| CreateBookCommand | Application | Use case input |

---

## Benefits

This structure provides:

- clear separation of concerns
- easier unit testing
- better maintainability
- less coupling to frameworks
- easier replacement of infrastructure
- cleaner business logic
- better alignment with production-grade microservice design

---

## Current scope

For the current stage, we only define and document the architecture.

We are not implementing controllers, services, repositories, entities, or database configuration yet.

Implementation will be introduced step by step in later stages.