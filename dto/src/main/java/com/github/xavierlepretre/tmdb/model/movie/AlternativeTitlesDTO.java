package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AlternativeTitlesDTO
{
    @NonNull private final MovieId id;
    @NonNull private final List<TitleDTO> titles;

    public AlternativeTitlesDTO(
            @JsonProperty(value = "id", required = true) @NonNull MovieId id,
            @JsonProperty(value = "titles", required = true) @NonNull List<TitleDTO> titles)
    {
        this.id = id;
        this.titles = titles;
    }

    @NonNull public MovieId getId()
    {
        return id;
    }

    @NonNull public List<TitleDTO> getTitles()
    {
        return titles;
    }
}
