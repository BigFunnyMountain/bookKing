package xyz.tomorrowlearncamp.bookking.domain.book.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.AddBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.SearchBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookStockRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.BookResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.SearchBookResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.service.BookService;
import xyz.tomorrowlearncamp.bookking.domain.book.service.SearchBookService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookController {
	private final BookService bookService;
	private final SearchBookService searchBookService;

	@PostMapping("/v1/books/search")
	@ResponseStatus(HttpStatus.OK)
	public SearchBookResponseDto searchBooks(@RequestBody SearchBookRequestDto requestDto) {
		return searchBookService.searchBooks(requestDto);
	}

	@PostMapping("/v1/books/import")
	public ResponseEntity<Void> importBooks(
		@RequestParam(defaultValue = "100") int pageSize,
		@RequestParam(defaultValue = "100") int totalPage
	){
		searchBookService.fetchBooksFromLibraryApi(pageSize, totalPage);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/v1/books")
	public ResponseEntity<Long> addBook(@RequestBody @Valid AddBookRequestDto requestDto) {
		return ResponseEntity.ok(bookService.addBook(requestDto));
	}

	@PatchMapping("/v1/books/{bookId}")
	public ResponseEntity<Void> updateBook(
		@PathVariable("bookId") Long bookId,
		@RequestBody @Valid UpdateBookRequestDto requestDto
	) {
		bookService.updateBook(bookId, requestDto);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/v1/books/{bookId}/stock")
	public ResponseEntity<Void> updateBookStock(
		@PathVariable("bookId") Long bookId,
		@RequestBody @Valid UpdateBookStockRequestDto requestDto
	) {
		bookService.updateBookStock(bookId, requestDto);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/v1/books/{bookId}")
	public ResponseEntity<Void> deleteBook(@PathVariable("bookId") Long bookId) {
		bookService.deleteBook(bookId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/v1/books")
	public ResponseEntity<Page<BookResponseDto>> getAllBooks(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(bookService.getAllBooks(pageable));
	}

	@GetMapping("/v1/books/{bookId}")
	public ResponseEntity<BookResponseDto> getBookById(@PathVariable("bookId") Long bookId) {
		return ResponseEntity.ok(bookService.getBookById(bookId));
	}
}
