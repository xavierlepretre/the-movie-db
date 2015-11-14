package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.NonNull;

public class AppendableRequestFactory
{
    @NonNull public AppendableRequest create(@NonNull String request)
    {
        return new AppendableRequest(request);
    }
}
