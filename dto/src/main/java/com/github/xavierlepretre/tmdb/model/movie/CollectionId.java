package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class CollectionId
{
    @NonNull private final Long id;

    @JsonCreator
    public CollectionId(long id)
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
        return obj instanceof CollectionId
                && ((CollectionId) obj).getId() == id;
    }

    @Override public String toString()
    {
        return "CollectionId{" +
                "id=" + id +
                '}';
    }
}
