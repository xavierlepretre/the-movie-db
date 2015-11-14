package com.github.xavierlepretre.tmdb.model.tag;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class KeywordId
{
    @NonNull private final Long id;

    @JsonCreator
    public KeywordId(long id)
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
        return obj instanceof KeywordId
                && ((KeywordId) obj).getId() == id;
    }
}
