package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class ImdbId
{
    @NonNull private final String id;

    @JsonCreator
    public ImdbId(@NonNull String id)
    {
        this.id = id;
    }

    @JsonValue @NonNull public String getId()
    {
        return id;
    }

    @Override public final int hashCode()
    {
        return id.hashCode();
    }

    @Override public final boolean equals(Object obj)
    {
        return obj instanceof ImdbId
                && ((ImdbId) obj).getId().equals(id);
    }

    @Override public String toString()
    {
        return "ImdbId{" +
                "id='" + id + '\'' +
                '}';
    }
}
