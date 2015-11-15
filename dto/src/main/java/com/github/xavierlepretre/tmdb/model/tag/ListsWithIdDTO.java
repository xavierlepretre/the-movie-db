package com.github.xavierlepretre.tmdb.model.tag;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import java.util.List;

public class ListsWithIdDTO extends ListsDTO
{
    @NonNull private final MovieId id;

    public ListsWithIdDTO(
            @JsonProperty(value = "id", required = true) @NonNull
            MovieId id,
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
        this.id = id;
    }

    @NonNull public MovieId getId()
    {
        return id;
    }
}
