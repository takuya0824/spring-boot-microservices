package com.micro.book_api.services;

import java.util.List;
import java.util.UUID;

import com.micro.book_api.dtos.BookDto;
import com.micro.book_api.dtos.CreateBookDto;
import com.micro.book_api.dtos.UpdateBookDto;

public interface BookService {
    public List<BookDto> getBooks();

    public BookDto getBook(UUID id);

    public BookDto createBook(CreateBookDto dto);

    public void deleteBook(UUID id);

    public void updateBook(UpdateBookDto dto, UUID id);
}
