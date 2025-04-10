package xyz.tomorrowlearncamp.bookking.domain.book.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookRequestDto;

import xyz.tomorrowlearncamp.bookking.domain.common.entity.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Book extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column
    private String isbn;

    @Column
    private String title;

    @Column
    private String subject;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String author;

    @Column
    private String publisher;

    @Column
    private String bookIntroductionUrl;

    @Column
    private String prePrice;

    @Column
    private String page;

    @Column
    private String titleUrl;

    @Column
    private String publicationDate;

    @Column(nullable = false)
    private Long stock = 0L;

    @Builder
    public Book(Long bookId, String isbn, String title, String subject, String author, String publisher,
        String bookIntroductionUrl, String prePrice, String page, String titleUrl, String publicationDate,
        Long stock) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.subject = subject;
        this.author = author;
        this.publisher = publisher;
        this.bookIntroductionUrl = bookIntroductionUrl;
        this.prePrice = prePrice;
        this.page = page;
        this.titleUrl = titleUrl;
        this.publicationDate = publicationDate;
        this.stock = stock;
    }

    public void updateStock(Long stock) {
        this.stock = stock;
    }
}
