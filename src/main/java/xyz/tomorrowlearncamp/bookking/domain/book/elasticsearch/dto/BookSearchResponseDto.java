package xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.dto;

import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.entity.ElasticBookDocument;

public record BookSearchResponseDto(
    Long bookId,
    String title,
    String author,
    String publisher,
    String subject
) {
    public static BookSearchResponseDto of(ElasticBookDocument elasticBookDocument) {
        return new BookSearchResponseDto(
            elasticBookDocument.bookId(),
            elasticBookDocument.title(),
            elasticBookDocument.author(),
            elasticBookDocument.publisher(),
            elasticBookDocument.subject()
        );
    }
}