package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.github.xavierlepretre.tmdb.model.TmdbContract.GenreEntity;
import com.github.xavierlepretre.tmdb.model.movie.GenreProviderDelegate;

public class EntityProviderDelegateFactory
{
    static final int GENRE_HELPER = 100;

    @NonNull public SparseArray<EntityProviderDelegate> createProviders()
    {
        SparseArray<EntityProviderDelegate> created = new SparseArray<>();
        created.put(GENRE_HELPER, createGenreProvider());
        return created;
    }

    @NonNull protected GenreProviderDelegate createGenreProvider()
    {
        return new GenreProviderDelegate(
                TmdbContract.CONTENT_AUTHORITY,
                GenreEntity.CONTENT_URI,
                GenreEntity.CONTENT_DIR_TYPE,
                GenreEntity.CONTENT_ITEM_TYPE);
    }
}
