package xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.dto.ElasticBookSearchResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.service.ElasticBookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ElasticBookSearchController {
    private final ElasticBookService elasticBookService;

    @GetMapping("/v1/elasticsearch")
    public ResponseEntity<Page<ElasticBookSearchResponseDto>> searchBooks(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        Page<ElasticBookSearchResponseDto> result = elasticBookService.search(keyword, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/v1/elasticsearch/autocomplete")
    public ResponseEntity<List<String>> searchAutoComplete(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(elasticBookService.searchAutoCompleteTitle(keyword, size));
    }

    @GetMapping("/v2/elasticsearch/autocomplete")
    public ResponseEntity<List<String>> searchAutocompleteV2(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(elasticBookService.searchAutoCompleteTitleV2(keyword, size));
    }

    @GetMapping("/v3/elasticsearch/autocomplete")
    public ResponseEntity<List<String>> searchAutocompleteV3(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(elasticBookService.searchAutoCompleteTitleV3(keyword, size));
    }

    @GetMapping("/v1/elasticsearch/relate")
    public ResponseEntity<List<String>> searchRelateKeywords(@RequestParam String keyword) {
        List<String> relateKeywords = elasticBookService.searchRelateKeywords(keyword);
        return ResponseEntity.ok(relateKeywords);
    }
}
