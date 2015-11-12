package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.NonNull;

public class MessageDTO
{
    @NonNull private final StatusCode statusCode;
    @NonNull private final String message;

    public MessageDTO(@NonNull StatusCode statusCode, @NonNull String message)
    {
        this.statusCode = statusCode;
        this.message = message;
    }

    @NonNull public StatusCode getStatusCode()
    {
        return statusCode;
    }

    @NonNull public String getMessage()
    {
        return message;
    }
}
