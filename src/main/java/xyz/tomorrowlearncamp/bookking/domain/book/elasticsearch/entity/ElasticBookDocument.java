package xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.entity;

import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;

public record ElasticBookDocument(
    Long bookId,
    String title,
    String author,
    String publisher,
    String subject
) {
    public static ElasticBookDocument of(Book book) {
        return new ElasticBookDocument(
            book.getBookId(),
            book.getTitle(),
            book.getAuthor(),
            book.getPublisher(),
            book.getSubject()
        );
    }
}
