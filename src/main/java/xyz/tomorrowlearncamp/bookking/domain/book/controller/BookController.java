package xyz.tomorrowlearncamp.bookking.domain.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.AddBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.SearchBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.request.UpdateBookStockRequestDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.BookResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.SearchBookResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.service.BookService;
import xyz.tomorrowlearncamp.bookking.domain.book.service.SearchBookService;
import xyz.tomorrowlearncamp.bookking.domain.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.domain.common.enums.ErrorMessage;
import xyz.tomorrowlearncamp.bookking.domain.common.exception.ForbiddenRequestException;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AuthUser;
import xyz.tomorrowlearncamp.bookking.domain.user.enums.UserRole;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookController {
	private final BookService bookService;
	private final SearchBookService searchBookService;

	@PostMapping("/v1/books/search")
	public Response<SearchBookResponseDto> searchBooks(@RequestBody SearchBookRequestDto requestDto) {
		return Response.success(searchBookService.searchBooks(requestDto));
	}

	@PostMapping("/v1/books/import")
	@ResponseStatus(HttpStatus.OK)
	public void importBooks(
			@AuthenticationPrincipal AuthUser user,
			@RequestParam(defaultValue = "100") int pageSize,
			@RequestParam(defaultValue = "100") int totalPage
	){
		if (!ObjectUtils.nullSafeEquals(user.getRole(), UserRole.ROLE_ADMIN)) {
			throw new ForbiddenRequestException(ErrorMessage.FORBIDDEN_ADMINISTRATOR);
		}
		searchBookService.fetchBooksFromLibraryApi(pageSize, totalPage);
	}

	@PostMapping("/v1/books")
	public Response<Long> addBook(
			@AuthenticationPrincipal AuthUser user,
			@RequestBody @Valid AddBookRequestDto requestDto
	) {
		if (!ObjectUtils.nullSafeEquals(user.getRole(), UserRole.ROLE_ADMIN)) {
			throw new ForbiddenRequestException(ErrorMessage.FORBIDDEN_ADMINISTRATOR);
		}
		return Response.success(bookService.addBook(requestDto));
	}

	@PatchMapping("/v1/books/{bookId}")
	@ResponseStatus(HttpStatus.OK)
	public void updateBook(
			@AuthenticationPrincipal AuthUser user,
			@PathVariable("bookId") Long bookId,
			@RequestBody @Valid UpdateBookRequestDto requestDto
	) {
		if (!ObjectUtils.nullSafeEquals(user.getRole(), UserRole.ROLE_ADMIN)) {
			throw new ForbiddenRequestException(ErrorMessage.FORBIDDEN_ADMINISTRATOR);
		}
		bookService.updateBook(bookId, requestDto);
	}

	@PatchMapping("/v1/books/{bookId}/stock")
	@ResponseStatus(HttpStatus.OK)
	public void updateBookStock(
			@AuthenticationPrincipal AuthUser user,
			@PathVariable("bookId") Long bookId,
			@RequestBody @Valid UpdateBookStockRequestDto requestDto
	) {
		if (!ObjectUtils.nullSafeEquals(user.getRole(), UserRole.ROLE_ADMIN)) {
			throw new ForbiddenRequestException(ErrorMessage.FORBIDDEN_ADMINISTRATOR);
		}
		bookService.updateBookStock(bookId, requestDto);
	}

	@DeleteMapping("/v1/books/{bookId}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteBook(
			@AuthenticationPrincipal AuthUser user,
			@PathVariable("bookId") Long bookId
	) {
		if (!ObjectUtils.nullSafeEquals(user.getRole(), UserRole.ROLE_ADMIN)) {
			throw new ForbiddenRequestException(ErrorMessage.FORBIDDEN_ADMINISTRATOR);
		}
		bookService.deleteBook(bookId);
	}

	@GetMapping("/v1/books")
	public Response<Page<BookResponseDto>> getAllBooks(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		return Response.success(bookService.getAllBooks(pageable));
	}

	@GetMapping("/v1/books/{bookId}")
	public Response<BookResponseDto> getBookById(@PathVariable("bookId") Long bookId) {
		return Response.success(bookService.getBookById(bookId));
	}
}
