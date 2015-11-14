package com.github.xavierlepretre.tmdb.model.tag;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class ListId
{
    @NonNull private final String id;

    @JsonCreator
    public ListId(@NonNull String id)
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
        return obj instanceof ListId
                && ((ListId) obj).getId().equals(id);
    }
}
