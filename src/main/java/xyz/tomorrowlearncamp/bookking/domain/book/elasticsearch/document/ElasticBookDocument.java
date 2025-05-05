package xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ElasticBookDocument {

    @JsonProperty("bookId")
    private Long bookId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author")
    private String author;

    @JsonProperty("publisher")
    private String publisher;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("source")
    private String source;

    @Builder
    private ElasticBookDocument(Long bookId, String title, String author, String publisher, String subject, String source) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.subject = subject;
        this.source = source;
    }

    public static ElasticBookDocument of(Book book) {
        return ElasticBookDocument.builder()
                .bookId(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .subject(book.getSubject())
                .source(book.getSource().name())
                .build();
    }
}