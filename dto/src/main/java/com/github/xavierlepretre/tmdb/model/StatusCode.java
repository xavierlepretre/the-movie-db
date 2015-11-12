package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.IntRange;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status Codes returned by the server.
 * List found here: https://www.themoviedb.org/documentation/api/status-codes
 */
public class StatusCode
{
    @IntRange(from = 1)
    private final int code;

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
}
