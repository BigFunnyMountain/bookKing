package xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.dto.ElasticBookSearchResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.service.ElasticBookService;
import xyz.tomorrowlearncamp.bookking.domain.common.dto.Response;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ElasticBookSearchController {
    private final ElasticBookService elasticBookService;

    @GetMapping("/v1/elasticsearch")
    public Response<Page<ElasticBookSearchResponseDto>> searchBooks(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        Page<ElasticBookSearchResponseDto> result = elasticBookService.search(keyword, pageable);
        return Response.success(result);
    }

    @GetMapping("/v1/elasticsearch/autocomplete")
    public Response<List<String>> searchAutoComplete(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Response.success(elasticBookService.searchAutoCompleteTitle(keyword, size));
    }

    @GetMapping("/v2/elasticsearch/autocomplete")
    public Response<List<String>> searchAutocompleteV2(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") int size
    ) {
        return Response.success(elasticBookService.searchAutoCompleteTitleV2(keyword, size));
    }

    @GetMapping("/v3/elasticsearch/autocomplete")
    public Response<List<String>> searchAutocompleteV3(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") int size
    ) {
        return Response.success(elasticBookService.searchAutoCompleteTitleV3(keyword, size));
    }

    @GetMapping("/v1/elasticsearch/relate")
    public Response<List<String>> searchRelateKeywords(@RequestParam String keyword) {
        List<String> relateKeywords = elasticBookService.searchRelateKeywords(keyword);
        return Response.success(relateKeywords);
    }
}
