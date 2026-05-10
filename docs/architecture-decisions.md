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
Accepted

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