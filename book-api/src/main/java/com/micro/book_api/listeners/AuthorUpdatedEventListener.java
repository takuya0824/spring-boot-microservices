package com.micro.book_api.listeners;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
public class AuthorUpdatedEventListener {
    private final AuthorRepository _authorRepository;
    private final BookRepository _bookRepository;

    @RabbitListener(queues = { RabbitMQConfig.QUEUE_AUTHOR_UPDATED })
    public void handleMessage(CustomMessage<AuthorEventDto> message) {
        log.info("{} got triggered. Message: {}", AuthorUpdatedEventListener.class, message.toString());
        AuthorEventDto authorEventDto = message.getPayload();
        Optional<Author> maybeExistingAuthor = _authorRepository.findById(authorEventDto.getId());
        if (maybeExistingAuthor.isEmpty()) {
            return;
        }
        Author existingAuthor = maybeExistingAuthor.get();
        existingAuthor.setName(authorEventDto.getName());
        existingAuthor.setDescription(authorEventDto.getDescription());

        List<Book> oldBooks = _bookRepository.findAllByAuthors(authorEventDto.getId());
        List<UUID> newBookIds = authorEventDto.getBooks();
        List<Book> newBooks = _bookRepository.findAllById(newBookIds);

        for (Book newBookItem : newBooks) {
            if (!newBookItem.getAuthors().contains(authorEventDto.getId())) {
                newBookItem.getAuthors().add(authorEventDto.getId());
            }
        }

        for (Book oldBookItem : oldBooks) {
            if (!newBookIds.contains(oldBookItem.getId())) {
                oldBookItem.getAuthors().remove(authorEventDto.getId());
            }
        }
    }
}
