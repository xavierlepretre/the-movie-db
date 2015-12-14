package com.github.xavierlepretre.tmdb.model.rate;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class ReviewId
{
    @NonNull private final String id;

    @JsonCreator
    public ReviewId(@NonNull String id)
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
        return obj instanceof ReviewId
                && ((ReviewId) obj).getId().equals(id);
    }

    @Override public String toString()
    {
        return "ReviewId{" +
                "id='" + id + '\'' +
                '}';
    }
}
