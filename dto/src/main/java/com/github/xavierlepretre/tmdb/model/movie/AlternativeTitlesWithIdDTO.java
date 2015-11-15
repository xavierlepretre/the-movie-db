package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AlternativeTitlesWithIdDTO extends AlternativeTitlesDTO
{
    @NonNull private final MovieId id;

    public AlternativeTitlesWithIdDTO(
            @JsonProperty(value = "id", required = true) @NonNull MovieId id,
            @JsonProperty(value = "titles", required = true) @NonNull List<TitleDTO> titles)
    {
        super(titles);
        this.id = id;
    }

    @NonNull public MovieId getId()
    {
        return id;
    }
}
