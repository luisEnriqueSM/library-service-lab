package com.tiangalo.lab.library.infrastructure.book.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataBookRepository extends JpaRepository<BookJpaEntity, UUID> {
    boolean existsByIsbn(String isbn);
    boolean existsByIsbnAndIdNot(String isbn, UUID id);
}