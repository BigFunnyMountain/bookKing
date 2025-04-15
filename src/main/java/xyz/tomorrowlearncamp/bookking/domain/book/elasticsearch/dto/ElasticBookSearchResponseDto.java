package xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.dto;

import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.entity.ElasticBookDocument;

public record ElasticBookSearchResponseDto(
    Long bookId,
    String title,
    String author,
    String publisher,
    String subject
) {
    public static ElasticBookSearchResponseDto of(ElasticBookDocument elasticBookDocument) {
        return new ElasticBookSearchResponseDto(
            elasticBookDocument.bookId(),
            elasticBookDocument.title(),
            elasticBookDocument.author(),
            elasticBookDocument.publisher(),
            elasticBookDocument.subject()
        );
    }
}