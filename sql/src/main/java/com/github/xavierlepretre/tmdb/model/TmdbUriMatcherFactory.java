package com.github.xavierlepretre.tmdb.model;

import android.content.UriMatcher;
import android.support.annotation.NonNull;
import android.util.SparseArray;

public class TmdbUriMatcherFactory
{
    @NonNull public UriMatcher create(@NonNull SparseArray<EntityProviderDelegate> providerDelegates)
    {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        int key;
        for (int i = 0; i < providerDelegates.size(); i++)
        {
            key = providerDelegates.keyAt(i);
            providerDelegates.get(key).registerWith(uriMatcher, key);
        }

        return uriMatcher;
    }
}
