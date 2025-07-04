package com.micro.book_api.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.micro.book_api.dtos.AuthorDto;
import com.micro.book_api.dtos.BookDto;
import com.micro.book_api.dtos.CreateBookDto;
import com.micro.book_api.dtos.UpdateBookDto;
import com.micro.book_api.mappers.AuthorMapper;
import com.micro.book_api.mappers.BookMapper;
import com.micro.book_api.models.Author;
import com.micro.book_api.models.Book;
import com.micro.book_api.repositories.AuthorRepository;
import com.micro.book_api.repositories.BookRepository;
import com.micro.shared.constants.RabbitMQKeys;
import com.micro.shared.domain.BookEventDto;
import com.micro.shared.errors.NotFoundException;
import com.micro.shared.models.CustomMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository _bookRepository;
    private final AuthorRepository _authorRepository;
    private final BookMapper _bookMapper;
    private final AuthorMapper _authorMapper;
    private final RabbitTemplate _template;

    @Override
    public List<BookDto> getBooks() {
        return _bookRepository.findAll().stream().map(bookItem -> _bookMapper.toDto(bookItem)).toList();
    }

    @Override
    public BookDto getBook(UUID id) {
        Book book = _findBookById(id);
        List<Author> authors = _authorRepository.findAllById(book.getAuthors());
        List<AuthorDto> authorDtos = authors.stream().map(author -> _authorMapper.toDto(author)).toList();
        BookDto bookDto = _bookMapper.toDto(book);
        bookDto.setAuthors(authorDtos);
        return bookDto;
    }

    @Override
    public BookDto createBook(CreateBookDto dto) {
        Book savedBook = _bookRepository.save(new Book(dto.getTitle(), dto.getDescription(), dto.getAuthors()));
        CustomMessage<BookEventDto> message = new CustomMessage<>();
        message.setMessageId(UUID.randomUUID());
        message.setMessageDate(LocalDateTime.now());
        message.setPayload(_bookMapper.toEventDto(savedBook));
        _template.convertAndSend(RabbitMQKeys.BOOK_CREATED_EXCHANGE, null, message);
        return _bookMapper.toDto(savedBook);
    }

    @Override
    public void deleteBook(UUID id) {
        Book book = _findBookById(id);
        _bookRepository.delete(book);
        CustomMessage<BookEventDto> message = new CustomMessage<>();
        message.setMessageId(UUID.randomUUID());
        message.setMessageDate(LocalDateTime.now());
        message.setPayload(_bookMapper.toEventDto(book));
        _template.convertAndSend(RabbitMQKeys.BOOK_DELETED_EXCHANGE, null, message);
    }

    @Override
    public void updateBook(UpdateBookDto dto, UUID id) {
        Book found = _findBookById(id);

        if (Objects.nonNull(dto.getTitle())) {
            found.setTitle(dto.getTitle());
        }

        if (Objects.nonNull(dto.getDescription())) {
            found.setDescription(dto.getDescription());
        }

        if (Objects.nonNull(dto.getAuthors())) {
            found.setAuthors(dto.getAuthors());
        }

        _bookRepository.save(found);

        CustomMessage<BookEventDto> message = new CustomMessage<>();
        message.setMessageId(UUID.randomUUID());
        message.setMessageDate(LocalDateTime.now());
        message.setPayload(_bookMapper.toEventDto(found));
        _template.convertAndSend(RabbitMQKeys.BOOK_UPDATED_EXCHANGE, null, message);
    }

    private Book _findBookById(UUID id) {
        Optional<Book> result = _bookRepository.findById(id);

        if (result.isEmpty()) {
            throw new NotFoundException("Not found with this ID: " + id);
        }

        return result.get();
    }

}
