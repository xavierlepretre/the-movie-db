package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.github.xavierlepretre.tmdb.model.TmdbContract.ConfigurationEntity;
import com.github.xavierlepretre.tmdb.model.TmdbContract.GenreEntity;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationProviderDelegate;
import com.github.xavierlepretre.tmdb.model.movie.GenreProviderDelegate;

public class EntityProviderDelegateFactory
{
    static final int CONFIGURATION_PROVIDER = 100;
    static final int GENRE_PROVIDER = 101;

    @NonNull public SparseArray<EntityProviderDelegate> createProviders()
    {
        SparseArray<EntityProviderDelegate> created = new SparseArray<>();
        created.put(CONFIGURATION_PROVIDER, createConfigurationProvider());
        created.put(GENRE_PROVIDER, createGenreProvider());
        return created;
    }

    @NonNull protected ConfigurationProviderDelegate createConfigurationProvider()
    {
        return new ConfigurationProviderDelegate(
                TmdbContract.CONTENT_AUTHORITY,
                ConfigurationEntity.CONTENT_URI,
                ConfigurationEntity.CONTENT_ITEM_TYPE);
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
