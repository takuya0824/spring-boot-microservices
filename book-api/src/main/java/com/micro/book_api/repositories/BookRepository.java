package com.micro.book_api.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.micro.book_api.models.Book;

public interface BookRepository extends JpaRepository<Book, UUID> {
  List<Book> findAllByAuthors(UUID authorId);
}
