package com.micro.author_api.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.micro.author_api.models.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {
    List<Author> findAllByBooks(UUID bookId);
}
