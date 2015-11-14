package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CollectionDTO
{
    @NonNull private final String backdropPath;
    @NonNull private final CollectionId id;
    @NonNull private final String name;
    @NonNull private final String posterPath;

    public CollectionDTO(
            @JsonProperty(value = "backdrop_path", required = true) @NonNull String backdropPath,
            @JsonProperty(value = "id", required = true) @NonNull CollectionId id,
            @JsonProperty(value = "name", required = true) @NonNull String name,
            @JsonProperty(value = "poster_path", required = true) @NonNull String posterPath)
    {
        this.backdropPath = backdropPath;
        this.id = id;
        this.name = name;
        this.posterPath = posterPath;
    }

    @NonNull public String getBackdropPath()
    {
        return backdropPath;
    }

    @NonNull public CollectionId getId()
    {
        return id;
    }

    @NonNull public String getName()
    {
        return name;
    }

    @NonNull public String getPosterPath()
    {
        return posterPath;
    }
}
