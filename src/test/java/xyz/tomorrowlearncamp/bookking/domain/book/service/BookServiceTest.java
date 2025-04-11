package xyz.tomorrowlearncamp.bookking.domain.book.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.AddBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookStockRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.BookResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.mapper.BookMapper;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BookMapper bookMapper;

	@Mock
	private JdbcTemplate jdbcTemplate;

	@InjectMocks
	private BookService bookService;

	@Test
	void addBook_shouldSaveBookAndReturnId() {
		// given
		AddBookRequestDto requestDto = new AddBookRequestDto();
		Book book = new Book();
		ReflectionTestUtils.setField(book, "bookId", 1L);

		given(bookMapper.toEntity(requestDto)).willReturn(book);
		given(bookRepository.save(book)).willReturn(book);

		// when
		Long savedId = bookService.addBook(requestDto);

		// then
		assertThat(savedId).isEqualTo(1L);
		then(bookRepository).should().save(book);
	}

	@Test
	void updateBook_shouldUpdateBookFields() {
		// given
		Long id = 1L;
		Book book = new Book();
		UpdateBookRequestDto requestDto = new UpdateBookRequestDto();

		given(bookRepository.findById(id)).willReturn(Optional.of(book));

		// when
		bookService.updateBook(id, requestDto);

		// then
		then(bookMapper).should().updateBookFromDto(requestDto, book);
	}

	@Test
	void updateBookStock_shouldUpdateStock() {
		// given
		Long id = 1L;
		Book book = mock(Book.class);
		UpdateBookStockRequestDto requestDto = new UpdateBookStockRequestDto();
		ReflectionTestUtils.setField(requestDto, "stock", 5L);

		given(bookRepository.findById(id)).willReturn(Optional.of(book));

		// when
		bookService.updateBookStock(id, requestDto);

		// then
		then(book).should().updateStock(5L);
	}

	@Test
	void getBookById_shouldReturnBookResponseDto() {
		// given
		Long id = 1L;
		Book book = new Book();

		given(bookRepository.findById(id)).willReturn(Optional.of(book));

		// when
		BookResponseDto response = bookService.getBookById(id);

		// then
		assertThat(response).isInstanceOf(BookResponseDto.class);
	}

	@Test
	void deleteBook_shouldCallRepositoryDelete() {
		// given
		Long id = 1L;

		// when
		bookService.deleteBook(id);

		// then
		then(bookRepository).should().deleteById(id);
	}

	@Test
	void saveBooksInBatch_shouldCallJdbcTemplateBatchUpdate() {
		// given
		List<Book> books = List.of(new Book(), new Book());

		// when
		bookService.saveBooksInBatch(books, 500);

		// then
		then(jdbcTemplate).should().batchUpdate(
			anyString(),
			eq(books),
			eq(500),
			any()
		);
	}
}