package com.github.xavierlepretre.tmdb.model.tag;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.PaginatedResultsDTO;

import java.util.List;

public class ListsDTO extends PaginatedResultsDTO<ListDTO>
{
    public ListsDTO(
            @JsonProperty(value = "page", required = true)
            int page,
            @JsonProperty(value = "results", required = true) @NonNull
            List<ListDTO> results,
            @JsonProperty(value = "total_pages", required = true)
            int totalPages,
            @JsonProperty(value = "total_results", required = true)
            int totalResults)
    {
        super(page, results, totalPages, totalResults);
    }
}
