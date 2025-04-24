package xyz.tomorrowlearncamp.bookking.domain.keyword.dto;

import lombok.Data;

import java.util.List;

@Data
public class KeywordResponse {
    private List<String> suggestedKeywords;
} 