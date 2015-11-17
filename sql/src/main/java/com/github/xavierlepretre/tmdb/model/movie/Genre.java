package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.Nullable;

public class Genre
{
    @Nullable private final GenreId id;
    @Nullable private final String name;

    public Genre(
            @Nullable GenreId id,
            @Nullable String name)
    {
        this.id = id;
        this.name = name;
    }

    @Nullable public GenreId getId()
    {
        return id;
    }

    @Nullable public String getName()
    {
        return name;
    }
}
