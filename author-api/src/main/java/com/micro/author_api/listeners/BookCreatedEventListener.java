package com.micro.author_api.listeners;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.micro.author_api.config.RabbitMQConfig;
import com.micro.author_api.mappers.BookMapper;
import com.micro.author_api.models.Author;
import com.micro.author_api.models.Book;
import com.micro.author_api.repositories.AuthorRepository;
import com.micro.author_api.repositories.BookRepository;
import com.micro.shared.domain.BookEventDto;
import com.micro.shared.models.CustomMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookCreatedEventListener {
    private final AuthorRepository _authorRepository;
    private final BookRepository _bookRepository;
    private final BookMapper _bookMapper;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_BOOK_CREATED)
    public void handleMessage(CustomMessage<BookEventDto> message) {
        log.info("{} got triggered. Message: {}", BookCreatedEventListener.class, message.toString());
        BookEventDto bookEventDto = message.getPayload();
        Optional<Book> result = _bookRepository.findById(bookEventDto.getId());
        if (result.isPresent()) {
            return;
        }

        Book newBook = _bookMapper.toEntity(bookEventDto);
        _bookRepository.save(newBook);

        List<UUID> authorIds = bookEventDto.getAuthors();
        if(Objects.isNull(authorIds) || authorIds.isEmpty()) {
            return;
        }

        List<Author> authors = _authorRepository.findAllById(authorIds);

        for(Author authorItem: authors) {
            if(!authorItem.getBooks().contains(bookEventDto.getId())) {
                authorItem.getBooks().add(bookEventDto.getId());
            }
        }
    }
}
