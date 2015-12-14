package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class GenreId
{
    @NonNull private final Integer id;

    @JsonCreator
    public GenreId(int id)
    {
        this.id = id;
    }

    @JsonValue public int getId()
    {
        return id;
    }

    @Override public final int hashCode()
    {
        return id.hashCode();
    }

    @Override public final boolean equals(Object obj)
    {
        return obj instanceof GenreId
                && ((GenreId) obj).getId() == id;
    }

    @Override public String toString()
    {
        return "GenreId{" +
                "id=" + id +
                '}';
    }
}
