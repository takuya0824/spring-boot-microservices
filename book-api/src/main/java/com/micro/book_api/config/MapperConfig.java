package com.micro.book_api.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.micro.book_api.dtos.BookDto;
import com.micro.book_api.models.Book;

@Configuration
public class MapperConfig {
    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper
                .typeMap(Book.class, BookDto.class)
                .addMappings(mapper -> mapper.map(src -> src.getAuthors(), BookDto::setAuthorIds));

        modelMapper.addMappings(new PropertyMap<Book, BookDto>() {
            @Override
            protected void configure() {
                skip(destination.getAuthors());
            }
        });
        return modelMapper;
    }
}
