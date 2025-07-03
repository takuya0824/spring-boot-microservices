package com.micro.author_api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.micro.author_api.models.Book;

public interface BookRepository extends JpaRepository<Book, UUID> {

}
