package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.image.ImagePath;

public class Collection
{
    @Nullable private final ImagePath backdropPath;
    @Nullable private final CollectionId id;
    @Nullable private final String name;
    @Nullable private final ImagePath posterPath;

    public Collection(
            @Nullable ImagePath backdropPath,
            @Nullable CollectionId id,
            @Nullable String name,
            @Nullable ImagePath posterPath)
    {
        this.backdropPath = backdropPath;
        this.id = id;
        this.name = name;
        this.posterPath = posterPath;
    }

    @Nullable public ImagePath getBackdropPath()
    {
        return backdropPath;
    }

    @Nullable public CollectionId getId()
    {
        return id;
    }

    @Nullable public String getName()
    {
        return name;
    }

    @Nullable public ImagePath getPosterPath()
    {
        return posterPath;
    }
}
