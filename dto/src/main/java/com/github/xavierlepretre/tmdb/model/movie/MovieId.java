package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class MovieId
{
    @NonNull private final Long id;

    @JsonCreator
    public MovieId(long id)
    {
        this.id = id;
    }

    @JsonValue public long getId()
    {
        return id;
    }

    @Override public final int hashCode()
    {
        return id.hashCode();
    }

    @Override public final boolean equals(Object obj)
    {
        return obj instanceof MovieId
                && ((MovieId) obj).getId() == id;
    }
}
