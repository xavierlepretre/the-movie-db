package com.github.xavierlepretre.tmdb.model.rate;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.PaginatedResultsDTO;

import java.util.List;

public class ReviewsDTO extends PaginatedResultsDTO<ReviewDTO>
{
    public ReviewsDTO(
            @JsonProperty(value = "page", required = true)
            int page,
            @JsonProperty(value = "results", required = true) @NonNull
            List<ReviewDTO> results,
            @JsonProperty(value = "total_pages", required = true)
            int totalPages,
            @JsonProperty(value = "total_results", required = true)
            int totalResults)
    {
        super(page,
                results,
                totalPages,
                totalResults);
    }
}
