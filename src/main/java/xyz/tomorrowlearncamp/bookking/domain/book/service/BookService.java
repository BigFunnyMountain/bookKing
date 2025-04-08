package xyz.tomorrowlearncamp.bookking.domain.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.AddBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookStockRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.BookResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.entity.Book;
import xyz.tomorrowlearncamp.bookking.domain.book.mapper.BookMapper;
import xyz.tomorrowlearncamp.bookking.domain.book.repository.BookRepository;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class BookService {
	private final BookRepository bookRepository;
	private final BookMapper bookMapper;

	@Transactional
	public Long addBook(AddBookRequestDto addBookRequestDto) {
		Book book = bookMapper.toEntity(addBookRequestDto);
		bookRepository.save(book);
		return book.getBookId();
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
}
