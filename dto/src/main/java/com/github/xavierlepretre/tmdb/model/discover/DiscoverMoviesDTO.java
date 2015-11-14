package com.github.xavierlepretre.tmdb.model.discover;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.PaginatedResultsDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieShortDTO;

import java.util.List;

public class DiscoverMoviesDTO extends PaginatedResultsDTO<MovieShortDTO>
{
    public DiscoverMoviesDTO(
            @JsonProperty(value = "page", required = true) int page,
            @JsonProperty(value = "results", required = true) @NonNull List<MovieShortDTO> results,
            @JsonProperty(value = "total_pages", required = true) int totalPages,
            @JsonProperty(value = "total_results", required = true) int totalResults)
    {
        super(page, results, totalPages, totalResults);
    }
}
