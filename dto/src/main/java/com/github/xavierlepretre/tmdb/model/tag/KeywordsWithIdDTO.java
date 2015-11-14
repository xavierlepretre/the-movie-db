package com.github.xavierlepretre.tmdb.model.tag;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import java.util.Collections;
import java.util.List;

public class KeywordsWithIdDTO
{
    @NonNull private final MovieId id;
    @NonNull private final List<KeywordDTO> keywords;

    public KeywordsWithIdDTO(
            @JsonProperty(value = "id", required = true) @NonNull MovieId id,
            @JsonProperty(value = "keywords", required = true) @NonNull List<KeywordDTO> keywords)
    {
        this.id = id;
        this.keywords = Collections.unmodifiableList(keywords);
    }

    @NonNull public MovieId getId()
    {
        return id;
    }

    @NonNull public List<KeywordDTO> getKeywords()
    {
        return keywords;
    }
}
