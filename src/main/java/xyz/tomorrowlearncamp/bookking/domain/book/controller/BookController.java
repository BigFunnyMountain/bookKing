package xyz.tomorrowlearncamp.bookking.domain.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.AddBookRequest;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.SearchBookRequest;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookRequest;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookStockRequest;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.BookResponse;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.SearchBookResponse;
import xyz.tomorrowlearncamp.bookking.domain.book.service.BookService;
import xyz.tomorrowlearncamp.bookking.domain.book.service.SearchBookService;
import xyz.tomorrowlearncamp.bookking.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.common.entity.AuthUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookController {

	private final BookService bookService;
	private final SearchBookService searchBookService;

	@PostMapping("/v1/books/search")
	public Response<SearchBookResponse> searchBooks(
		@Valid @RequestBody SearchBookRequest requestDto
	) {
		return Response.success(searchBookService.searchBooks(requestDto));
	}

	@PostMapping("/v1/books/import")
	public void importBooks(
		@RequestParam(defaultValue = "100") int pageSize,
		@RequestParam(defaultValue = "100") int totalPage,
		@RequestParam(defaultValue = "1") int startPage
	) {
		searchBookService.fetchBooksFromLibraryApi(pageSize, totalPage, startPage);
	}

	@PostMapping("/v1/books")
	public Response<Long> addBook(
		@Valid @RequestBody AddBookRequest requestDto
	) {
		return Response.success(bookService.addBook(requestDto));
	}

	@PatchMapping("/v1/books/{bookId}")
	public void updateBook(
		@PathVariable("bookId") Long bookId,
		@Valid @RequestBody UpdateBookRequest requestDto
	) {
		bookService.updateBook(bookId, requestDto);
	}

	@PatchMapping("/v1/books/{bookId}/stock")
	public void updateBookStock(
		@PathVariable("bookId") Long bookId,
		@RequestBody @Valid UpdateBookStockRequest requestDto
	) {
		bookService.updateBookStock(bookId, requestDto);
	}

	@DeleteMapping("/v1/books/{bookId}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteBook(
		@AuthenticationPrincipal AuthUser user,
		@PathVariable("bookId") Long bookId
	) {
		bookService.deleteBook(bookId);
	}

	@GetMapping("/v1/books")
	public Response<Page<BookResponse>> getAllBooks(Pageable pageable) {
		return Response.success(bookService.getAllBooks(pageable));
	}

	@GetMapping("/v1/books/keywords")
	public Response<Page<BookResponse>> getAllBooksByKeyword(
		@RequestParam String keyword,
		Pageable pageable
	) {
		return Response.success(bookService.getAllBooksByKeyword(keyword, pageable));
	}

	@GetMapping("/v1/books/{bookId}")
	public Response<BookResponse> getBookById(@PathVariable("bookId") Long bookId) {
		return Response.success(bookService.getBookById(bookId));
	}
}
