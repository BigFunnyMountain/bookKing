package xyz.tomorrowlearncamp.bookking.domain.keyword.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KeywordRequest {
    @NotBlank(message = "Search term is required")
    private String searchTerm;

    private KeywordRequest(String searchTerm) {
        this.searchTerm = searchTerm;
    }
} 