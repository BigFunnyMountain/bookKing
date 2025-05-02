package xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.document.ElasticBookDocument;

@Getter
@NoArgsConstructor
public class ElasticBookSearchResponse {

    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private String subject;
    private String source;

    @Builder
    private ElasticBookSearchResponse(Long bookId, String title, String author, String publisher, String subject, String source) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.subject = subject;
        this.source = source;
    }

    public static ElasticBookSearchResponse of(ElasticBookDocument doc) {
        return ElasticBookSearchResponse.builder()
                .bookId(doc.getBookId())
                .title(doc.getTitle())
                .author(doc.getAuthor())
                .publisher(doc.getPublisher())
                .subject(doc.getSubject())
                .source(doc.getSource())
                .build();
    }
}