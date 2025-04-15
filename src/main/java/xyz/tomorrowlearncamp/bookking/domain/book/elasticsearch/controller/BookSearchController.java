package xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.domain.book.dto.response.BookResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.dto.BookSearchResponseDto;
import xyz.tomorrowlearncamp.bookking.domain.book.elasticsearch.service.ElasticBookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookSearchController {
    private final ElasticBookService elasticBookService;

    @GetMapping("/v1/elasticsearch")
    public ResponseEntity<List<BookSearchResponseDto>> searchBooks(@RequestParam String keyword) {
        List<BookSearchResponseDto> results = elasticBookService.search(keyword);
        return ResponseEntity.ok(results);
    }


}
