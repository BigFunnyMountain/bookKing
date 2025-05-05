package xyz.tomorrowlearncamp.bookking.domain.keyword.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.bookking.common.dto.Response;
import xyz.tomorrowlearncamp.bookking.domain.keyword.dto.KeywordRequest;
import xyz.tomorrowlearncamp.bookking.domain.keyword.dto.KeywordResponse;
import xyz.tomorrowlearncamp.bookking.domain.keyword.service.KeywordService;
import xyz.tomorrowlearncamp.bookking.common.entity.AuthUser;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KeywordController {
    private final KeywordService keywordService;

    @PostMapping("/v1/keywords/suggest")
    public Response<KeywordResponse> suggestKeywords(@Valid @RequestBody KeywordRequest request) {
        return Response.success(keywordService.suggestKeywords(request.getSearchTerm()));
    }

    @GetMapping("/v1/keywords/recommendations")
    public Response<KeywordResponse> getPersonalizedRecommendations(
            @AuthenticationPrincipal AuthUser authUser) {
        return Response.success(keywordService.suggestByOrder(authUser.getUserId()));
    }
} 