package xyz.tomorrowlearncamp.bookking.domain.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.common.entity.BaseEntity;

@Getter
@Entity
@NoArgsConstructor
public class Book extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

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
    private String publicationDate;

    @Column(nullable = false)
    private Long stock = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookSource source;

    @Builder
    public Book(Long bookId, String title, String subject, String author, String publisher,
        String bookIntroductionUrl, String prePrice, String publicationDate,
        Long stock, BookSource source) {
        this.bookId = bookId;
        this.title = title;
        this.subject = subject;
        this.author = author;
        this.publisher = publisher;
        this.bookIntroductionUrl = bookIntroductionUrl;
        this.prePrice = prePrice;
        this.publicationDate = publicationDate;
        this.stock = stock;
        this.source = source;
    }

    public void updateStock(Long stock) {
        this.stock = stock;
    }
}
