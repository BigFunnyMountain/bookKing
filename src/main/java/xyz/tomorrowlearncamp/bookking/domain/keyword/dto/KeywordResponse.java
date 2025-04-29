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

    public static KeywordResponse of(List<String> suggestedKeywords) {
        KeywordResponse response = new KeywordResponse();
        response.suggestedKeywords = suggestedKeywords;
        return response;
    }
}