package com.github.xavierlepretre.tmdb.model.show;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class VideoId
{
    @NonNull private final String id;

    @JsonCreator
    public VideoId(@NonNull String id)
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
        return obj instanceof VideoId
                && ((VideoId) obj).getId().equals(id);
    }
}
