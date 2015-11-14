package com.github.xavierlepretre.tmdb.model.show;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import java.util.List;

public class ReleasesWithIdDTO extends ReleasesDTO
{
    @NonNull private final MovieId id;

    public ReleasesWithIdDTO(
            @JsonProperty(value = "countries", required = true) @NonNull
            List<ReleaseCountryDTO> countries,
            @JsonProperty(value = "id", required = true) @NonNull
            MovieId id)
    {
        super(countries);
        this.id = id;
    }

    @NonNull public MovieId getId()
    {
        return id;
    }
}
