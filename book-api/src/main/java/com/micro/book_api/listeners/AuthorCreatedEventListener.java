package com.micro.book_api.listeners;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.micro.book_api.config.RabbitMQConfig;
import com.micro.book_api.mappers.AuthorMapper;
import com.micro.book_api.models.Author;
import com.micro.book_api.models.Book;
import com.micro.book_api.repositories.AuthorRepository;
import com.micro.book_api.repositories.BookRepository;
import com.micro.shared.domain.AuthorEventDto;
import com.micro.shared.models.CustomMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorCreatedEventListener {

    private final AuthorRepository _authorRepository;
    private final BookRepository _bookRepository;
    private final AuthorMapper _authorMapper;

    @RabbitListener(queues = { RabbitMQConfig.QUEUE_AUTHOR_CREATED })
    public void handleMessage(CustomMessage<AuthorEventDto> message) {
        log.info("{} got triggered. got a message: {}", AuthorCreatedEventListener.class,
                message.getPayload().toString());

        AuthorEventDto authorEventDto = message.getPayload();
        Optional<Author> existingAuthor = _authorRepository.findById(authorEventDto.getId());
        if (existingAuthor.isPresent()) {
            return;
        }
        Author newAuthor = _authorMapper.toEntity(authorEventDto);
        _authorRepository.save(newAuthor);

        List<UUID> bookIds = authorEventDto.getBooks();
        if (Objects.isNull(bookIds) || bookIds.isEmpty()) {
            return;
        }

        List<Book> books = _bookRepository.findAllById(bookIds);

        for (Book bookItem : books) {
            if (!bookItem.getAuthors().contains(authorEventDto.getId())) {
                bookItem.getAuthors().add(authorEventDto.getId());
            }
        }
    }
}
