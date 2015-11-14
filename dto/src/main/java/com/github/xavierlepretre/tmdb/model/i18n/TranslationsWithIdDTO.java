package com.github.xavierlepretre.tmdb.model.i18n;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import java.util.List;

public class TranslationsWithIdDTO extends TranslationsDTO
{
    @NonNull private final MovieId id;

    public TranslationsWithIdDTO(
            @JsonProperty(value = "id", required = true) @NonNull
            MovieId id,
            @JsonProperty(value = "translations", required = true) @NonNull
            List<TranslationDTO> translations)
    {
        super(translations);
        this.id = id;
    }

    @NonNull public MovieId getId()
    {
        return id;
    }
}
