# API Contracts

Base path:

```text
/api/v1
```

---

## Create book

```http
POST /api/v1/books
```

Creates a new book.

### Request

```json
{
  "title": "Clean Architecture",
  "author": "Robert C. Martin",
  "isbn": "9780134494166",
  "category": "SOFTWARE_ENGINEERING",
  "publicationYear": 2017
}
```

### Response `201 Created`

```json
{
  "id": "8f2a7f6a-1c11-4b1b-8f5d-4d6a1d1d9c10",
  "title": "Clean Architecture",
  "author": "Robert C. Martin",
  "isbn": "9780134494166",
  "category": "SOFTWARE_ENGINEERING",
  "publicationYear": 2017,
  "status": "ACTIVE",
  "createdAt": "2026-05-04T23:00:00Z",
  "updatedAt": "2026-05-04T23:00:00Z"
}
```

---

## Get book by id

```http
GET /api/v1/books/{bookId}
```

Returns a book by its identifier.

### Path parameters

| Name | Type | Required | Description |
|---|---|---|---|
| bookId | UUID | Yes | Book identifier |

### Response `200 OK`

```json
{
  "id": "8f2a7f6a-1c11-4b1b-8f5d-4d6a1d1d9c10",
  "title": "Clean Architecture",
  "author": "Robert C. Martin",
  "isbn": "9780134494166",
  "category": "SOFTWARE_ENGINEERING",
  "publicationYear": 2017,
  "status": "ACTIVE",
  "createdAt": "2026-05-04T23:00:00Z",
  "updatedAt": "2026-05-04T23:00:00Z"
}
```

---

## Search books

```http
GET /api/v1/books
```

Searches books using optional filters.

By default, only `ACTIVE` books are returned.

### Query parameters

| Name | Type | Required | Description |
|---|---|---|---|
| title | String | No | Partial title match |
| author | String | No | Partial author match |
| category | BookCategory | No | Exact category match |
| status | BookStatus | No | Exact status match. Defaults to `ACTIVE` |

### Example

```http
GET /api/v1/books?title=clean&author=martin&category=SOFTWARE_ENGINEERING&status=ACTIVE
```

### Response `200 OK`

```json
[
  {
    "id": "8f2a7f6a-1c11-4b1b-8f5d-4d6a1d1d9c10",
    "title": "Clean Architecture",
    "author": "Robert C. Martin",
    "isbn": "9780134494166",
    "category": "SOFTWARE_ENGINEERING",
    "publicationYear": 2017,
    "status": "ACTIVE",
    "createdAt": "2026-05-04T23:00:00Z",
    "updatedAt": "2026-05-04T23:00:00Z"
  }
]
```

---

## Update book

```http
PUT /api/v1/books/{bookId}
```

Updates editable book information.

This operation does not update the book status.

### Path parameters

| Name | Type | Required | Description |
|---|---|---|---|
| bookId | UUID | Yes | Book identifier |

### Request

```json
{
  "title": "Clean Architecture",
  "author": "Robert C. Martin",
  "isbn": "9780134494166",
  "category": "SOFTWARE_ENGINEERING",
  "publicationYear": 2017
}
```

### Response `200 OK`

```json
{
  "id": "8f2a7f6a-1c11-4b1b-8f5d-4d6a1d1d9c10",
  "title": "Clean Architecture",
  "author": "Robert C. Martin",
  "isbn": "9780134494166",
  "category": "SOFTWARE_ENGINEERING",
  "publicationYear": 2017,
  "status": "ACTIVE",
  "createdAt": "2026-05-04T23:00:00Z",
  "updatedAt": "2026-05-04T23:10:00Z"
}
```

---

## Deactivate book

```http
PATCH /api/v1/books/{bookId}/deactivate
```

Deactivates a book.

No request body required.

### Path parameters

| Name | Type | Required | Description |
|---|---|---|---|
| bookId | UUID | Yes | Book identifier |

### Response `200 OK`

```json
{
  "id": "8f2a7f6a-1c11-4b1b-8f5d-4d6a1d1d9c10",
  "status": "INACTIVE"
}
```

---

# Error responses

## Validation error

Used when the request body or parameters are invalid.

### Response `400 Bad Request`

```json
{
  "timestamp": "2026-05-04T23:00:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "path": "/api/v1/books",
  "details": [
    {
      "field": "title",
      "message": "title is required"
    }
  ]
}
```

---

## Book not found

Used when the requested book does not exist.

### Response `404 Not Found`

```json
{
  "timestamp": "2026-05-04T23:00:00Z",
  "status": 404,
  "error": "BOOK_NOT_FOUND",
  "message": "Book not found",
  "path": "/api/v1/books/8f2a7f6a-1c11-4b1b-8f5d-4d6a1d1d9c10"
}
```

---

## ISBN already exists

Used when a book with the provided ISBN already exists.

### Response `409 Conflict`

```json
{
  "timestamp": "2026-05-04T23:00:00Z",
  "status": 409,
  "error": "ISBN_ALREADY_EXISTS",
  "message": "A book with the provided ISBN already exists",
  "path": "/api/v1/books"
}
```