package com.micro.author_api.services;

import java.util.List;
import java.util.UUID;

import com.micro.author_api.dtos.AuthorDto;
import com.micro.author_api.dtos.CreateAuthorDto;
import com.micro.author_api.dtos.UpdateAuthorDto;

public interface AuthorService {
    public List<AuthorDto> getAuthors();

    public AuthorDto getAuthor(UUID id);

    public AuthorDto createAuthor(CreateAuthorDto dto);

    public void deleteAuthor(UUID id);

    public void updateAuthor(UpdateAuthorDto dto, UUID id);
}
