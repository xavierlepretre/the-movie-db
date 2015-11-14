package com.github.xavierlepretre.tmdb.model.people;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import java.util.List;

public class CreditsWithIdDTO extends CreditsDTO
{
    @NonNull private final MovieId id;

    public CreditsWithIdDTO(
            @JsonProperty(value = "cast", required = true) @NonNull List<CastDTO> cast,
            @JsonProperty(value = "crew", required = true) @NonNull List<CrewDTO> crew,
            @JsonProperty(value = "id", required = true) @NonNull MovieId id)
    {
        super(cast, crew);
        this.id = id;
    }

    @NonNull public MovieId getId()
    {
        return id;
    }
}
