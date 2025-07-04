package com.micro.author_api.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.micro.author_api.dtos.AuthorDto;
import com.micro.author_api.dtos.BookDto;
import com.micro.author_api.dtos.CreateAuthorDto;
import com.micro.author_api.dtos.UpdateAuthorDto;
import com.micro.author_api.mappers.AuthorMapper;
import com.micro.author_api.mappers.BookMapper;
import com.micro.author_api.models.Author;
import com.micro.author_api.models.Book;
import com.micro.author_api.repositories.AuthorRepository;
import com.micro.author_api.repositories.BookRepository;
import com.micro.shared.constants.RabbitMQKeys;
import com.micro.shared.domain.AuthorEventDto;
import com.micro.shared.errors.NotFoundException;
import com.micro.shared.models.CustomMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository _authorRepository;
    private final AuthorMapper _authorMapper;
    private final BookRepository _bookRepository;
    private final BookMapper _bookMapper;
    private final RabbitTemplate _template;

    @Override
    public List<AuthorDto> getAuthors() {
        return _authorRepository.findAll().stream().map(author -> _authorMapper.toDto(author)).toList();
    }

    @Override
    public AuthorDto getAuthor(UUID id) {
        Author author = _findAuthorById(id);
        List<Book> books = _bookRepository.findAllById(author.getBooks());
        List<BookDto> bookDtos = books.stream().map(book -> _bookMapper.toDto(book)).toList();
        AuthorDto authorDto = _authorMapper.toDto(author);
        authorDto.setBooks(bookDtos);
        return authorDto;
    }

    @Override
    public AuthorDto createAuthor(CreateAuthorDto dto) {
        Author savedAuthor = _authorRepository.save(new Author(dto.getName(), dto.getDescription(), dto.getBooks()));
        CustomMessage<AuthorEventDto> message = new CustomMessage<>();
        message.setMessageId(UUID.randomUUID());
        message.setMessageDate(LocalDateTime.now());
        message.setPayload(_authorMapper.toEventDto(savedAuthor));
        _template.convertAndSend(RabbitMQKeys.AUTHOR_CREATED_EXCHANGE, null, message);
        return _authorMapper.toDto(savedAuthor);
    }

    @Override
    public void deleteAuthor(UUID id) {
        Author author = _findAuthorById(id);
        _authorRepository.delete(author);
        CustomMessage<AuthorEventDto> message = new CustomMessage<>();
        message.setMessageId(UUID.randomUUID());
        message.setMessageDate(LocalDateTime.now());
        message.setPayload(_authorMapper.toEventDto(author));
        _template.convertAndSend(RabbitMQKeys.AUTHOR_DELETED_EXCHANGE, null, message);
    }

    @Override
    public void updateAuthor(UpdateAuthorDto dto, UUID id) {
        Author existingAuthor = _findAuthorById(id);

        if (Objects.nonNull(dto.getName())) {
            existingAuthor.setName(dto.getName());
        }

        if (Objects.nonNull(dto.getDescription())) {
            existingAuthor.setDescription(dto.getDescription());
        }

        if (Objects.nonNull(dto.getBooks())) {
            existingAuthor.setBooks(dto.getBooks());
        }

        _authorRepository.save(existingAuthor);

        CustomMessage<AuthorEventDto> message = new CustomMessage<>();
        message.setMessageId(UUID.randomUUID());
        message.setMessageDate(LocalDateTime.now());
        message.setPayload(_authorMapper.toEventDto(existingAuthor));
        _template.convertAndSend(RabbitMQKeys.AUTHOR_UPDATED_EXCHANGE, null, message);
    }

    private Author _findAuthorById(UUID id) {
        Optional<Author> result = _authorRepository.findById(id);

        if (result.isEmpty()) {
            throw new NotFoundException("Not found with this ID: " + id);
        }

        return result.get();
    }
}
