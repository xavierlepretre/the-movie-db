package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status Codes returned by the server.
 * List found here: https://www.themoviedb.org/documentation/api/status-codes
 */
public class StatusCode
{
    @IntRange(from = 1) @NonNull
    private final Integer code;

    @JsonCreator
    public StatusCode(@IntRange(from = 1) int code)
    {
        this.code = code;
    }

    @JsonValue @IntRange(from = 1)
    public int getCode()
    {
        return code;
    }

    @Override public final int hashCode()
    {
        return code.hashCode();
    }

    @Override public final boolean equals(Object obj)
    {
        return obj instanceof StatusCode
                && ((StatusCode) obj).code.equals(code);
    }
}
