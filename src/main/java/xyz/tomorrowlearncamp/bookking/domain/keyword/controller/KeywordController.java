package xyz.tomorrowlearncamp.bookking.domain.keyword.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.domain.keyword.dto.KeywordRequest;
import xyz.tomorrowlearncamp.bookking.domain.keyword.dto.KeywordResponse;
import xyz.tomorrowlearncamp.bookking.domain.keyword.service.KeywordService;
import xyz.tomorrowlearncamp.bookking.domain.user.auth.dto.AuthUser;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KeywordController {
    private final KeywordService keywordService;

    @PostMapping("/v1/keywords/suggest")
    public ResponseEntity<KeywordResponse> suggestKeywords(@Valid @RequestBody KeywordRequest request) {
        return ResponseEntity.ok(keywordService.suggestKeywords(request.getSearchTerm()));
    }

    @GetMapping("/v1/keywords/recommendations")
    public ResponseEntity<KeywordResponse> getPersonalizedRecommendations(
            @AuthenticationPrincipal AuthUser authUser) {
        return ResponseEntity.ok(keywordService.suggestByOrder(authUser.getUserId()));
    }
} 