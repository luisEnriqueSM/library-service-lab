# Library Service Lab

Hands-on lab to practice building a production-style microservice using:

- Java 21
- Spring Boot 4
- Hexagonal Architecture
- Spring Data JPA
- MySQL
- H2
- Docker
- Swagger / OpenAPI
- Kubernetes
- AWS ECS/EKS

The goal is to build the service step by step, focusing on architecture, clean code, testing, persistence, containerization, and deployment best practices.

---

## Current capabilities

The service currently supports a Book API with the following operations:

- Create a book
- Get a book by id
- Search books
- Update a book
- Deactivate a book

The service follows a hexagonal architecture approach:

- Domain layer
- Application layer
- Infrastructure layer
- API adapters
- Persistence adapters

---

## Requirements

Before running the project, make sure you have:

- Java 21
- Docker
- Maven Wrapper included in the project

You can verify Java with:

```bash
java -version
```

You can verify Docker with:

```bash
docker --version
```

---

## Run tests

Run the full test suite:

```bash
./mvnw test
```

---

## Run locally with H2

Use the `dev` profile to run the application with an in-memory H2 database:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{
  "groups": ["liveness", "readiness"],
  "status": "UP"
}
```

---

## Run locally with MySQL using Docker

Start MySQL:

```bash
docker compose up -d
```

Check containers:

```bash
docker compose ps
```

Run the application using the `docker` profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=docker
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```

---

## MySQL connection

The Docker profile uses the following local database configuration:

| Property | Value |
|---|---|
| Database | `library_db` |
| User | `library_user` |
| Password | `library_password` |
| Port | `3306` |

To connect directly to MySQL:

```bash
docker exec -it library-mysql mysql -u library_user -p library_db
```

Password:

```text
library_password
```

Useful SQL commands:

```sql
SHOW TABLES;
SELECT * FROM books;
```

Exit MySQL:

```sql
exit;
```

---

## Stop Docker services

Stop MySQL while keeping persisted data:

```bash
docker compose down
```

Stop MySQL and remove the database volume:

```bash
docker compose down -v
```

Use `down -v` only when you want to reset the local database.

---

## API documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Book API

Base path:

```text
/api/books
```

Available endpoints:

| Method | Path | Description |
|---|---|---|
| POST | `/api/books` | Create a book |
| GET | `/api/books/{id}` | Get a book by id |
| GET | `/api/books` | Search books |
| PUT | `/api/books/{id}` | Update a book |
| DELETE | `/api/books/{id}` | Deactivate a book |

---

## Example request

Create a book:

```bash
curl -i -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "author": "Uncle Bob",
    "isbn": "9780132350884",
    "category": "SOFTWARE_ENGINEERING",
    "publicationYear": 2008
  }'
```

Search active books:

```bash
curl -i "http://localhost:8080/api/books?status=ACTIVE"
```

---

## Project documentation

Additional documentation is available under the `docs` directory:

- Architecture decisions
- Domain model
- API contracts
- Hexagonal architecture notes