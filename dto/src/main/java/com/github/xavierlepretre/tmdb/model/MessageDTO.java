package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageDTO
{
    @NonNull private final StatusCode statusCode;
    @NonNull private final String message;

    public MessageDTO(
            @JsonProperty(value = "status_code", required = true) @NonNull StatusCode statusCode,
            @JsonProperty(value = "status_message", required = true) @NonNull String message)
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
