# Persistence Model

## Service responsibility

The goal of this layer is to map the Database Table using JPA.

---

### BookJpaEntity

A `BookJpaEntity` represents the persistence model for the `books` database table.

| Field            | Type               | Description | Constraints      |
|------------------|--------------------|---|------------------|
| id               | UUID / VARCHAR(36) | Unique book identifier | Primary key      |
| title            | VARCHAR(255)       | Book title | Not Null         |
| author           | VARCHAR(255)       | Main author name | Not Null         |
| isbn             | VARCHAR(32)        | Unique ISBN | Not Null, Unique |
| category         | VARCHAR(50)        | Book category | Not Null         |
| publication_year | INT                | Year when the book was published | Not Null         |
| status           | VARCHAR(20)        | Current lifecycle status of the book | Not Null         |
| created_at       | TIMESTAMP          | Creation timestamp | Not Null         |
| updated_at       | TIMESTAMP          | Last update timestamp | Not Null         |