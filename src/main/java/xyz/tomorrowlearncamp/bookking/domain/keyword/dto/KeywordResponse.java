package xyz.tomorrowlearncamp.bookking.domain.keyword.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class KeywordResponse {
    private List<String> suggestedKeywords;

    @Builder
    private KeywordResponse(List<String> suggestedKeywords) {
        this.suggestedKeywords = suggestedKeywords;
    }

    public static KeywordResponse of(List<String> suggestedKeywords) {
        return KeywordResponse.builder()
                .suggestedKeywords(suggestedKeywords)
                .build();
    }
} 