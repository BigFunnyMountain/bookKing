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
import xyz.tomorrowlearncamp.bookking.domain.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AuthUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookController {

	private final BookService bookService;
	private final SearchBookService searchBookService;

	@PostMapping("/v1/books/search")
	public Response<SearchBookResponse> searchBooks(@RequestBody SearchBookRequest requestDto) {
		return Response.success(searchBookService.searchBooks(requestDto));
	}

	@PostMapping("/v1/books/import")
	@ResponseStatus(HttpStatus.OK)
	public void importBooks(
		@AuthenticationPrincipal AuthUser user,
		@RequestParam(defaultValue = "100") int pageSize,
		@RequestParam(defaultValue = "100") int totalPage,
		@RequestParam(defaultValue = "1") int startPage
	) {
//		if (!ObjectUtils.nullSafeEquals(user.getRole(), UserRole.ROLE_ADMIN)) {
//			throw new ForbiddenRequestException(ErrorMessage.FORBIDDEN_ADMINISTRATOR);
//		}
		searchBookService.fetchBooksFromLibraryApi(pageSize, totalPage, startPage);
	}

	@PostMapping("/v1/books")
	public Response<Long> addBook(
		@AuthenticationPrincipal AuthUser user,
		@RequestBody @Valid AddBookRequest requestDto
	) {
//		if (!ObjectUtils.nullSafeEquals(user.getRole(), UserRole.ROLE_ADMIN)) {
//			throw new ForbiddenRequestException(ErrorMessage.FORBIDDEN_ADMINISTRATOR);
//		}
		return Response.success(bookService.addBook(requestDto));
	}

	@PatchMapping("/v1/books/{bookId}")
	@ResponseStatus(HttpStatus.OK)
	public void updateBook(
		@AuthenticationPrincipal AuthUser user,
		@PathVariable("bookId") Long bookId,
		@RequestBody @Valid UpdateBookRequest requestDto
	) {
//		if (!ObjectUtils.nullSafeEquals(user.getRole(), UserRole.ROLE_ADMIN)) {
//			throw new ForbiddenRequestException(ErrorMessage.FORBIDDEN_ADMINISTRATOR);
//		}
		bookService.updateBook(bookId, requestDto);
	}

	@PatchMapping("/v1/books/{bookId}/stock")
	@ResponseStatus(HttpStatus.OK)
	public void updateBookStock(
		@AuthenticationPrincipal AuthUser user,
		@PathVariable("bookId") Long bookId,
		@RequestBody @Valid UpdateBookStockRequest requestDto
	) {
//		if (!ObjectUtils.nullSafeEquals(user.getRole(), UserRole.ROLE_ADMIN)) {
//			throw new ForbiddenRequestException(ErrorMessage.FORBIDDEN_ADMINISTRATOR);
//		}
		bookService.updateBookStock(bookId, requestDto);
	}

	@DeleteMapping("/v1/books/{bookId}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteBook(
		@AuthenticationPrincipal AuthUser user,
		@PathVariable("bookId") Long bookId
	) {
//		if (!ObjectUtils.nullSafeEquals(user.getRole(), UserRole.ROLE_ADMIN)) {
//			throw new ForbiddenRequestException(ErrorMessage.FORBIDDEN_ADMINISTRATOR);
//		}
		bookService.deleteBook(bookId);
	}

	@GetMapping("/v1/books")
	public Response<Page<BookResponse>> getAllBooks(Pageable pageable) {
		return Response.success(bookService.getAllBooks(pageable));
	}

	@GetMapping("/v1/books/keyword")
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
