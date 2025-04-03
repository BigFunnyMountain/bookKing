package xyz.tomorrowlearncamp.bookking.domain.book.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private Long count;
}
