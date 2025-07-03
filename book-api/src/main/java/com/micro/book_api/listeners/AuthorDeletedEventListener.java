package com.micro.book_api.listeners;

import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.micro.book_api.config.RabbitMQConfig;
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
@Transactional
public class AuthorDeletedEventListener {
    private final BookRepository _bookRepository;
    private final AuthorRepository _authorRepository;

    @RabbitListener(queues = { RabbitMQConfig.QUEUE_AUTHOR_DELETED })
    public void handleMessage(CustomMessage<AuthorEventDto> message) {
        log.info("{} got triggered. got a message: {}", AuthorDeletedEventListener.class,
        message.getPayload().toString());

        AuthorEventDto authorEventDto = message.getPayload();
        Optional<Author> existingAuthor = _authorRepository.findById(authorEventDto.getId());
        if (existingAuthor.isEmpty()) {
            return;
        }
        _authorRepository.delete(existingAuthor.get());

        List<Book> books = _bookRepository.findAllByAuthors(authorEventDto.getId());

        for (Book bookItem : books) {
            bookItem.getAuthors().remove(authorEventDto.getId());
        }
    }
}
