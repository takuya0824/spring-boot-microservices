package com.micro.book_api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.micro.book_api.models.Author;

public interface AuthorRepository extends JpaRepository<Author, UUID> {

}
