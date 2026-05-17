# Domain Model

## Service responsibility

The `library-service` manages books in a library system.

The goal of this service is to provide a small but realistic domain for practicing Spring Boot, REST APIs, validation, persistence, testing, and hexagonal architecture.

---

## Main domain concept

### Book

A `Book` represents a book registered in the library catalog.

| Field | Type | Description |
|---|---|---|
| id | BookId | Unique book identifier |
| title | String | Book title |
| author | String | Main author name |
| isbn | String | Unique ISBN |
| category | BookCategory | Book category |
| publicationYear | Integer | Year when the book was published |
| status | BookStatus | Current lifecycle status of the book |
| createdAt | Instant | Creation timestamp |
| updatedAt | Instant | Last update timestamp |

---

## BookId

Id for the Book class.

| Field | Type | Description |
|---|---|---|
| id | UUID | Unique book identifier |

## BookStatus

Possible values:

| Status | Description |
|---|---|
| ACTIVE | The book is available in the catalog |
| INACTIVE | The book was deactivated and should not appear in default searches |

---

## BookCategory

Initial supported categories:

| Category | Description |
|---|---|
| SOFTWARE_ENGINEERING | Software engineering and architecture books |
| COMPUTER_SCIENCE | Computer science fundamentals |
| DATA_SCIENCE | Data science and analytics books |
| ARTIFICIAL_INTELLIGENCE | AI, machine learning, and related topics |
| BUSINESS | Business and management books |
| OTHER | Fallback category for uncategorized books |

---

## Business rules

- `title` is required.
- `author` is required.
- `isbn` is required.
- `isbn` must be unique.
- `category` is required.
- `publicationYear` cannot be in the future.
- A new book starts with `ACTIVE` status.
- An `INACTIVE` book should not appear in default search results.
- The book status cannot be changed through the regular update operation.
- A book is deactivated using a dedicated operation.

---

## Out of scope for V1

The following concepts are intentionally excluded from the first version:

- Loans
- Reservations
- Users
- Multiple authors
- Publishers
- Book copies
- Ratings
- Reviews