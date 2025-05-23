package xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.dto.ElasticBookSearchResponse;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.service.ElasticBookService;
import xyz.tomorrowlearncamp.bookking.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.common.entity.AuthUser;
import xyz.tomorrowlearncamp.bookking.domain.book.service.BookService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ElasticBookSearchController {
	private final ElasticBookService elasticBookService;
	private final BookService bookService;

	@GetMapping("/v2/books/search")
	public Response<Page<ElasticBookSearchResponse>> searchBooks(Pageable pageable) {
		Page<ElasticBookSearchResponse> result = elasticBookService.search(pageable);
		return Response.success(result);
	}

	@GetMapping("/v1/books/search/keywords")
	public Response<Page<ElasticBookSearchResponse>> searchBooksByKeyword(
   		@AuthenticationPrincipal AuthUser user,
		@RequestParam String keyword,
		Pageable pageable
	) {
		Page<ElasticBookSearchResponse> result = elasticBookService.searchByKeyword(user.getUserId(), keyword, pageable);
		return Response.success(result);
	}

	@PostMapping("/v1/books/search/reindex")
	public void reindexBooks(
		@RequestParam(defaultValue = "500") int pageSize,
		@RequestParam(defaultValue = "0") int startPage,
		@RequestParam(defaultValue = "100") int endPage
	) {
		bookService.reindexBooks(pageSize, startPage, endPage);
	}

	@GetMapping("/v1/books/search/autocomplete")
	public Response<List<String>> searchAutoComplete(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "10") int size
	) {
		return Response.success(elasticBookService.searchAutoCompleteTitle(keyword, size));
	}

	@GetMapping("/v2/books/search/autocomplete")
	public Response<List<String>> searchAutocompleteV2(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "5") int size
	) {
		return Response.success(elasticBookService.searchAutoCompleteTitleV2(keyword, size));
	}

	@GetMapping("/v3/books/search/autocomplete")
	public Response<List<String>> searchAutocompleteV3(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "5") int size
	) {
		return Response.success(elasticBookService.searchAutoCompleteTitleV3(keyword, size));
	}

	@GetMapping("/v1/books/search/relate")
	public Response<List<String>> searchRelateKeywords(@RequestParam String keyword) {
		List<String> relateKeywords = elasticBookService.searchRelateKeywords(keyword);
		return Response.success(relateKeywords);
	}
}
