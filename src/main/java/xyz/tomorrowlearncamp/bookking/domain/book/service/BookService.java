package xyz.tomorrowlearncamp.bookking.domain.book.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.AddBookRequest;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookRequest;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookStockRequest;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.BookResponse;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.service.ElasticBookService;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.mapper.BookMapper;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.common.exception.NotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final JdbcTemplate jdbcTemplate;
    private final ElasticBookService elasticBookService;

    private static final String INSERT_SQL = """
                INSERT INTO books (
                    title, subject, author, publisher,
                    book_introduction_url, pre_price,
                    publication_date, stock, created_at, modified_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    @Transactional
    public void saveBooksInBatch(List<Book> books, int batchSize) {
        LocalDateTime now = LocalDateTime.now();

        books.forEach(elasticBookService::save);

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
    public Long addBook(AddBookRequest addBookRequest) {
        Book book = bookMapper.toEntity(addBookRequest);
        Book saved = bookRepository.save(book);
        elasticBookService.save(saved);
        return saved.getId();
    }

    @Transactional
    public void updateBook(Long id, UpdateBookRequest requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.BOOK_NOT_FOUND));
        bookMapper.updateBookFromDto(requestDto, book);
    }

    @Transactional
    public void updateBookStock(Long id, UpdateBookStockRequest requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.BOOK_NOT_FOUND));
        book.updateStock(requestDto.getStock());
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
            .map(BookResponse::of);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.BOOK_NOT_FOUND));
        return new BookResponse(book);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooksByKeyword(String keyword, Pageable pageable) {
        return bookRepository.findAllByKeyword(keyword, pageable)
            .map(BookResponse::of);
    }

    @Transactional(readOnly = true)
    public void reindexBooks(int pageSize, int startPage, int endPage) { // 페이지 하나의 사이즈, 읽어올 페이지의 수, 시작할 페이지
        long totalIndexed = 0;
        int currentPage = startPage;
        boolean hasNextPages = true;

        while (hasNextPages && currentPage < endPage) {
            Page<Book> pagingBook = bookRepository.findAll(PageRequest.of(currentPage, pageSize));
            List<Book> books = pagingBook.getContent();

            if (books.isEmpty()) {
                break;
            }

            elasticBookService.reindexBulkInsert(books);

            totalIndexed += books.size();
            log.info("현재 페이지: {}, 색인된 데이터의 수: {}", currentPage, totalIndexed);

            currentPage++;
            hasNextPages = (currentPage < pagingBook.getTotalPages());
        }
    }

    private String convertString(Object value, String defaultValue){
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof String str) {
			return str.isEmpty() ? defaultValue : str;
        } else if (value instanceof String[] arr) {
			return arr.length > 0 ? String.join(",", arr) : defaultValue;
        } else {
            return value.toString();
        }
    }
}
