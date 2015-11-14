package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class PaginatedResultsDTO<T>
{
    @NonNull private final Integer page;
    @NonNull private final List<T> results;
    @NonNull private final Integer totalPages;
    @NonNull private final Integer totalResults;

    public PaginatedResultsDTO(
            @JsonProperty(value = "page", required = true) int page,
            @JsonProperty(value = "results", required = true) @NonNull List<T> results,
            @JsonProperty(value = "total_pages", required = true) int totalPages,
            @JsonProperty(value = "total_results", required = true) int totalResults)
    {
        this.page = page;
        this.results = Collections.unmodifiableList(results);
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

     public int getPage()
    {
        return page;
    }

    @NonNull public List<T> getResults()
    {
        return results;
    }

    public int getTotalPages()
    {
        return totalPages;
    }

    public int getTotalResults()
    {
        return totalResults;
    }
}
