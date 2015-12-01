package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class GenreListDTO
{
    @NonNull private final List<GenreDTO> genres;

    public GenreListDTO(
            @JsonProperty(value = "genres", required = true) @NonNull
            List<GenreDTO> genres)
    {
        this.genres = Collections.unmodifiableList(genres);
    }

    @NonNull public List<GenreDTO> getGenres()
    {
        return genres;
    }
}
