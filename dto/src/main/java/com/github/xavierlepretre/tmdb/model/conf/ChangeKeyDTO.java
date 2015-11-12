package com.github.xavierlepretre.tmdb.model.conf;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class ChangeKeyDTO
{
    @NonNull private final String key;

    @JsonCreator
    public ChangeKeyDTO(@NonNull String key)
    {
        this.key = key;
    }

    @NonNull @JsonValue
    public String getKey()
    {
        return key;
    }

    @Override public int hashCode()
    {
        return key.hashCode();
    }

    @Override public boolean equals(Object obj)
    {
        return obj instanceof ChangeKeyDTO
                && ((ChangeKeyDTO) obj).getKey().equals(key);
    }
}
