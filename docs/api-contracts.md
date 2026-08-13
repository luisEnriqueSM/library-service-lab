# API Contracts

## Book API

Base path:

```http
/api/books
```

Swagger UI:

```http
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```http
http://localhost:8080/v3/api-docs
```

---

## Create book

```http
POST /api/books
```

Creates a new book and returns the created resource.

### Request body

```json
{
  "title": "Clean Code",
  "author": "Uncle Bob",
  "isbn": "9780132350884",
  "category": "SOFTWARE_ENGINEERING",
  "publicationYear": 2008
}
```

### Response `201 Created`

```json
{
  "id": "8f2a7f6a-1c11-4b1b-8f5d-4d6a1d1d9c10",
  "title": "Clean Code",
  "author": "Uncle Bob",
  "isbn": "9780132350884",
  "category": "SOFTWARE_ENGINEERING",
  "publicationYear": 2008,
  "status": "ACTIVE",
  "createdAt": "2026-08-09T19:05:55.598695Z",
  "updatedAt": "2026-08-09T19:05:55.598695Z"
}
```

### Responses

| Status | Description |
|---|---|
| 201 | Book created successfully |
| 400 | Invalid request payload |
| 409 | ISBN already exists |

---

## Get book by id

```http
GET /api/books/{id}
```

Returns a book by its unique identifier.

### Path parameters

| Name | Type | Required | Description |
|---|---|---|---|
| id | UUID | Yes | Book unique identifier |

### Response `200 OK`

```json
{
  "id": "8f2a7f6a-1c11-4b1b-8f5d-4d6a1d1d9c10",
  "title": "Clean Code",
  "author": "Uncle Bob",
  "isbn": "9780132350884",
  "category": "SOFTWARE_ENGINEERING",
  "publicationYear": 2008,
  "status": "ACTIVE",
  "createdAt": "2026-08-09T19:05:55.598695Z",
  "updatedAt": "2026-08-09T19:05:55.598695Z"
}
```

### Responses

| Status | Description |
|---|---|
| 200 | Book found |
| 404 | Book not found |

---

## Search books

```http
GET /api/books
```

Searches books using optional filters such as title, author, category, and status.

### Query parameters

| Name | Type | Required | Description |
|---|---|---|---|
| title | string | No | Filters books by title |
| author | string | No | Filters books by author |
| category | string | No | Filters books by category |
| status | string | No | Filters books by status |

### Example

```http
GET /api/books?title=clean&author=bob&category=SOFTWARE_ENGINEERING&status=ACTIVE
```

### Response `200 OK`

```json
[
  {
    "id": "8f2a7f6a-1c11-4b1b-8f5d-4d6a1d1d9c10",
    "title": "Clean Code",
    "author": "Uncle Bob",
    "isbn": "9780132350884",
    "category": "SOFTWARE_ENGINEERING",
    "publicationYear": 2008,
    "status": "ACTIVE",
    "createdAt": "2026-08-09T19:05:55.598695Z",
    "updatedAt": "2026-08-09T19:05:55.598695Z"
  }
]
```

### Responses

| Status | Description |
|---|---|
| 200 | Books found |
| 400 | Invalid search filter |

---

## Update book

```http
PUT /api/books/{id}
```

Updates an existing book by its unique identifier.

### Path parameters

| Name | Type | Required | Description |
|---|---|---|---|
| id | UUID | Yes | Book unique identifier |

### Request body

```json
{
  "title": "Clean Architecture",
  "author": "Uncle Bob",
  "isbn": "9780132350885",
  "category": "SOFTWARE_ENGINEERING",
  "publicationYear": 2017
}
```

### Response `200 OK`

```json
{
  "id": "8f2a7f6a-1c11-4b1b-8f5d-4d6a1d1d9c10",
  "title": "Clean Architecture",
  "author": "Uncle Bob",
  "isbn": "9780132350885",
  "category": "SOFTWARE_ENGINEERING",
  "publicationYear": 2017,
  "status": "ACTIVE",
  "createdAt": "2026-08-09T19:05:55.598695Z",
  "updatedAt": "2026-08-09T19:19:45.728397Z"
}
```

### Responses

| Status | Description |
|---|---|
| 200 | Book updated successfully |
| 400 | Invalid request payload |
| 404 | Book not found |
| 409 | ISBN already exists |

---

## Deactivate book

```http
DELETE /api/books/{id}
```

Deactivates a book by its unique identifier and returns the updated resource.

### Path parameters

| Name | Type | Required | Description |
|---|---|---|---|
| id | UUID | Yes | Book unique identifier |

### Response `200 OK`

```json
{
  "id": "8f2a7f6a-1c11-4b1b-8f5d-4d6a1d1d9c10",
  "title": "Clean Architecture",
  "author": "Uncle Bob",
  "isbn": "9780132350885",
  "category": "SOFTWARE_ENGINEERING",
  "publicationYear": 2017,
  "status": "INACTIVE",
  "createdAt": "2026-08-09T19:05:55.598695Z",
  "updatedAt": "2026-08-09T19:23:13.105151Z"
}
```

### Responses

| Status | Description |
|---|---|
| 200 | Book deactivated successfully |
| 404 | Book not found |

---

## Book categories

Current supported values:

```text
SOFTWARE_ENGINEERING
COMPUTER_SCIENCE
DATA_SCIENCE
ARTIFICIAL_INTELLIGENCE
BUSINESS
OTHER
```

---

## Book statuses

Current supported values:

```text
ACTIVE
INACTIVE
```

---

## Error responses

The API returns a standard error response for handled application and validation errors.

### Example

```json
{
  "code": "BOOK_NOT_FOUND",
  "message": "Book not found",
  "timestamp": "2026-08-09T19:23:13.105151Z"
}
```

### Current error codes

| Code | HTTP status | Description |
|---|---:|---|
| BAD_REQUEST | 400 | Invalid request, invalid enum value, or invalid domain input |
| VALIDATION_ERROR | 400 | Request validation failed |
| BOOK_NOT_FOUND | 404 | Book does not exist |
| DUPLICATED_ISBN | 409 | ISBN already exists |