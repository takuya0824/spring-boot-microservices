package com.micro.book_api.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.micro.book_api.dtos.BookDto;
import com.micro.book_api.models.Book;
import com.micro.shared.domain.BookEventDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookMapper {
    private final ModelMapper _modelMapper;

    public BookDto toDto(Book entity) {
        return _modelMapper.map(entity, BookDto.class);
    }

    public BookEventDto toEventDto(Book entity) {
        return _modelMapper.map(entity, BookEventDto.class);
    }
}
