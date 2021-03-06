package com.github.xavierlepretre.tmdb.model.tag;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import java.util.List;

public class KeywordsWithIdDTO extends KeywordsDTO
{
    @NonNull private final MovieId id;

    public KeywordsWithIdDTO(
            @JsonProperty(value = "id", required = true) @NonNull MovieId id,
            @JsonProperty(value = "keywords", required = true) @NonNull List<KeywordDTO> keywords)
    {
        super(keywords);
        this.id = id;
    }

    @NonNull public MovieId getId()
    {
        return id;
    }
}
