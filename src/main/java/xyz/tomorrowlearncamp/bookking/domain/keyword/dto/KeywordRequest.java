package xyz.tomorrowlearncamp.bookking.domain.keyword.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KeywordRequest {
    @NotBlank(message = "Search term is required")
    private String searchTerm;
} 