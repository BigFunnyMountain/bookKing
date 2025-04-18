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

}
