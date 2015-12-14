package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.github.xavierlepretre.tmdb.model.TmdbContract.CollectionEntity;
import com.github.xavierlepretre.tmdb.model.TmdbContract.ConfigurationEntity;
import com.github.xavierlepretre.tmdb.model.TmdbContract.GenreEntity;
import com.github.xavierlepretre.tmdb.model.TmdbContract.MovieEntity;
import com.github.xavierlepretre.tmdb.model.TmdbContract.ProductionCompanyEntity;
import com.github.xavierlepretre.tmdb.model.TmdbContract.ProductionCountryEntity;
import com.github.xavierlepretre.tmdb.model.TmdbContract.SpokenLanguageEntity;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationProviderDelegate;
import com.github.xavierlepretre.tmdb.model.i18n.SpokenLanguageProviderDelegate;
import com.github.xavierlepretre.tmdb.model.movie.CollectionProviderDelegate;
import com.github.xavierlepretre.tmdb.model.movie.GenreProviderDelegate;
import com.github.xavierlepretre.tmdb.model.movie.MovieProviderDelegate;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyProviderDelegate;
import com.github.xavierlepretre.tmdb.model.production.ProductionCountryProviderDelegate;

public class EntityProviderDelegateFactory
{
    static final int COLLECTION_PROVIDER = 100;
    static final int CONFIGURATION_PROVIDER = 101;
    static final int GENRE_PROVIDER = 102;
    static final int MOVIE_PROVIDER = 103;
    static final int PRODUCTION_COMPANY_PROVIDER = 104;
    static final int PRODUCTION_COUNTRY_PROVIDER = 105;
    static final int SPOKEN_LANGUAGE_PROVIDER = 106;

    @NonNull public SparseArray<EntityProviderDelegate> createProviders()
    {
        SparseArray<EntityProviderDelegate> created = new SparseArray<>();
        created.put(COLLECTION_PROVIDER, createCollectionProvider());
        created.put(CONFIGURATION_PROVIDER, createConfigurationProvider());
        created.put(GENRE_PROVIDER, createGenreProvider());
        created.put(MOVIE_PROVIDER, createMovieProvider());
        created.put(PRODUCTION_COMPANY_PROVIDER, createProductionCompanyProvider());
        created.put(PRODUCTION_COUNTRY_PROVIDER, createProductionCountryProvider());
        created.put(SPOKEN_LANGUAGE_PROVIDER, createSpokenLanguageProvider());
        return created;
    }

    @NonNull protected CollectionProviderDelegate createCollectionProvider()
    {
        return new CollectionProviderDelegate(
                TmdbContract.CONTENT_AUTHORITY,
                CollectionEntity.CONTENT_URI,
                CollectionEntity.CONTENT_DIR_TYPE,
                CollectionEntity.CONTENT_ITEM_TYPE);
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

    @NonNull protected MovieProviderDelegate createMovieProvider()
    {
        return new MovieProviderDelegate(
                TmdbContract.CONTENT_AUTHORITY,
                MovieEntity.CONTENT_URI,
                MovieEntity.CONTENT_DIR_TYPE,
                MovieEntity.CONTENT_ITEM_TYPE);
    }

    @NonNull protected ProductionCompanyProviderDelegate createProductionCompanyProvider()
    {
        return new ProductionCompanyProviderDelegate(
                TmdbContract.CONTENT_AUTHORITY,
                ProductionCompanyEntity.CONTENT_URI,
                ProductionCompanyEntity.CONTENT_DIR_TYPE,
                ProductionCompanyEntity.CONTENT_ITEM_TYPE);
    }

    @NonNull protected ProductionCountryProviderDelegate createProductionCountryProvider()
    {
        return new ProductionCountryProviderDelegate(
                TmdbContract.CONTENT_AUTHORITY,
                ProductionCountryEntity.CONTENT_URI,
                ProductionCountryEntity.CONTENT_DIR_TYPE,
                ProductionCountryEntity.CONTENT_ITEM_TYPE);
    }

    @NonNull protected SpokenLanguageProviderDelegate createSpokenLanguageProvider()
    {
        return new SpokenLanguageProviderDelegate(
                TmdbContract.CONTENT_AUTHORITY,
                SpokenLanguageEntity.CONTENT_URI,
                SpokenLanguageEntity.CONTENT_DIR_TYPE,
                SpokenLanguageEntity.CONTENT_ITEM_TYPE);
    }
}
