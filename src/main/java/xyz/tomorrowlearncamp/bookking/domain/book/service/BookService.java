package xyz.tomorrowlearncamp.bookking.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

}
