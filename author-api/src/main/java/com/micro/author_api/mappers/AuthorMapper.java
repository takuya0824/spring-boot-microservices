package com.micro.author_api.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.micro.author_api.dtos.AuthorDto;
import com.micro.author_api.models.Author;
import com.micro.shared.domain.AuthorEventDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthorMapper {
    private final ModelMapper _modelMapper;

    public AuthorDto toDto(Author entity) {
        return _modelMapper.map(entity, AuthorDto.class);
    }

    public AuthorEventDto toEventDto(Author entity) {
        return _modelMapper.map(entity, AuthorEventDto.class);
    }
}
