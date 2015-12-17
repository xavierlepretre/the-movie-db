package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.image.HasBackdropPathDTO;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;

public class CollectionDTO implements HasBackdropPathDTO
{
    @NonNull private final ImagePath backdropPath;
    @NonNull private final CollectionId id;
    @NonNull private final String name;
    @NonNull private final ImagePath posterPath;

    public CollectionDTO(
            @JsonProperty(value = "backdrop_path", required = true) @NonNull ImagePath backdropPath,
            @JsonProperty(value = "id", required = true) @NonNull CollectionId id,
            @JsonProperty(value = "name", required = true) @NonNull String name,
            @JsonProperty(value = "poster_path", required = true) @NonNull ImagePath posterPath)
    {
        this.backdropPath = backdropPath;
        this.id = id;
        this.name = name;
        this.posterPath = posterPath;
    }

    @NonNull @Override public ImagePath getBackdropPath()
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

    @NonNull public ImagePath getPosterPath()
    {
        return posterPath;
    }
}
