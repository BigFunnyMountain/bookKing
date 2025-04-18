package xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.document.ElasticBookDocument;

@Getter
@NoArgsConstructor
public class ElasticBookSearchResponseDto {

    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private String subject;

    @Builder
    private ElasticBookSearchResponseDto(Long bookId, String title, String author, String publisher, String subject) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.subject = subject;
    }

    public static ElasticBookSearchResponseDto of(ElasticBookDocument elasticBookDocument) {
        return ElasticBookSearchResponseDto.builder()
                .bookId(elasticBookDocument.getBookId())
                .title(elasticBookDocument.getTitle())
                .author(elasticBookDocument.getAuthor())
                .publisher(elasticBookDocument.getPublisher())
                .subject(elasticBookDocument.getSubject())
                .build();
    }
}