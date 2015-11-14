package com.github.xavierlepretre.tmdb.model.tag;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KeywordDTO
{
    @NonNull private final KeywordId id;
    @NonNull private final String name;

    public KeywordDTO(
            @JsonProperty(value = "id", required = true) @NonNull
            KeywordId id,
            @JsonProperty(value = "name", required = true) @NonNull
            String name)
    {
        this.id = id;
        this.name = name;
    }

    @NonNull public KeywordId getId()
    {
        return id;
    }

    @NonNull public String getName()
    {
        return name;
    }
}
