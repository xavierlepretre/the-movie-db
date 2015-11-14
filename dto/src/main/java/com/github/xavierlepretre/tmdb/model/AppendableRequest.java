package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.NonNull;

public class AppendableRequest
{
    @NonNull private final String request;

    public AppendableRequest(@NonNull String request)
    {
        this.request = request;
    }

    @Override public String toString()
    {
        return request;
    }

    @Override public final int hashCode()
    {
        return request.hashCode();
    }

    @Override public final boolean equals(Object obj)
    {
        return obj instanceof AppendableRequest
                && obj.toString().equals(request);
    }
}
