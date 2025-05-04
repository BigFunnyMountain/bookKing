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

    // todo : 무슨 의도인지 모르겠습니다.
    private KeywordRequest(String searchTerm) {
        this.searchTerm = searchTerm;
    }
} 