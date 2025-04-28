package xyz.tomorrowlearncamp.bookking.domain.book.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.AddBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookStockRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.BookResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.document.ElasticBookDocument;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.service.ElasticBookService;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.mapper.BookMapper;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final JdbcTemplate jdbcTemplate;
    private final ElasticBookService elasticBookService;

    private static final String INSERT_SQL = """
                INSERT INTO book (
                    title, subject, author, publisher,
                    book_introduction_url, pre_price,
                    publication_date, stock, created_at, modified_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    public void saveBooksInBatch(List<Book> books, int batchSize) {
        LocalDateTime now = LocalDateTime.now();

        books.forEach(book -> {
            elasticBookService.save(book);
        });

        jdbcTemplate.batchUpdate(INSERT_SQL, books, batchSize,
                (ps, book) -> {
                    ps.setString(1, convertString(book.getTitle(),"제목없음"));
                    ps.setString(2, convertString(book.getSubject(), "주제없음"));
                    ps.setString(3, convertString(book.getAuthor(), "저자없음"));
                    ps.setString(4, convertString(book.getPublisher(), "출판사없음"));
                    ps.setString(5, convertString(book.getBookIntroductionUrl(), "소개없음"));
                    ps.setString(6, convertString(book.getPrePrice(), "가격없음"));
                    ps.setString(7, convertString(book.getPublicationDate(),"출판날자없음"));
                    ps.setLong(8, 0L);
                    ps.setTimestamp(9, Timestamp.valueOf(now));
                    ps.setTimestamp(10, Timestamp.valueOf(now));
                });
    }

    @Transactional
    public Long addBook(AddBookRequestDto addBookRequestDto) {
        Book book = bookMapper.toEntity(addBookRequestDto);
        Book saved = bookRepository.save(book);
        elasticBookService.save(saved);
        return saved.getBookId();
    }

    @Transactional
    public void updateBook(Long id, UpdateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
        bookMapper.updateBookFromDto(requestDto, book);
    }

    @Transactional
    public void updateBookStock(Long id, UpdateBookStockRequestDto requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
        book.updateStock(requestDto.getStock());
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<BookResponseDto> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(BookResponseDto::of);
    }

    @Transactional(readOnly = true)
    public BookResponseDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
        return new BookResponseDto(book);
    }

    private String convertString(Object value, String defaultValue){
        if(value == null) return defaultValue;

        if(value instanceof String){
            String str = (String) value;
            return str.isEmpty() ? defaultValue : str;
        } else if(value instanceof String[]){
            String[] arr = (String[]) value;
            return arr.length > 0 ? String.join(",", arr) : defaultValue;
        } else {
            return value.toString();
        }
    }
}
