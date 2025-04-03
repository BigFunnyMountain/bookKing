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

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private String bookIntroductionUrl;

    @Column(nullable = false)
    private Long prePrice;

    @Column(nullable = false)
    private Long page;

    @Column(nullable = false)
    private String titleUrl;

    @Column(nullable = false)
    private LocalDateTime publicationDate;

    @Column(nullable = false)
    private Long stock;

    @Builder
    public Book(Long bookId, String isbn, String title, String subject, String author, String publisher,
        String bookIntroductionUrl, Long prePrice, Long page, String titleUrl, LocalDateTime publicationDate,
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

    public void updateStock(Long stock){
        this.stock = stock;
    }
}
