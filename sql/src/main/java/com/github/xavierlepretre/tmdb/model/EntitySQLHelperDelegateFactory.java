package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationSQLHelperDelegate;
import com.github.xavierlepretre.tmdb.model.i18n.SpokenLanguageSQLHelperDelegate;
import com.github.xavierlepretre.tmdb.model.movie.CollectionSQLHelperDelegate;
import com.github.xavierlepretre.tmdb.model.movie.GenreSQLHelperDelegate;
import com.github.xavierlepretre.tmdb.model.movie.MovieGenreSQLHelperDelegate;
import com.github.xavierlepretre.tmdb.model.movie.MovieSQLHelperDelegate;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanySQLHelperDelegate;
import com.github.xavierlepretre.tmdb.model.production.ProductionCountrySQLHelperDelegate;

public class EntitySQLHelperDelegateFactory
{
    static final int COLLECTION_PROVIDER = 100;
    static final int CONFIGURATION_PROVIDER = 101;
    static final int GENRE_PROVIDER = 102;
    static final int MOVIE_PROVIDER = 103;
    static final int MOVIE_GENRE_PROVIDER = 104;
    static final int PRODUCTION_COMPANY_PROVIDER = 105;
    static final int PRODUCTION_COUNTRY_PROVIDER = 106;
    static final int SPOKEN_LANGUAGE_PROVIDER = 107;

    @NonNull public SparseArray<EntitySQLHelperDelegate> createHelpers()
    {
        SparseArray<EntitySQLHelperDelegate> created = new SparseArray<>();
        created.put(COLLECTION_PROVIDER, new CollectionSQLHelperDelegate());
        created.put(CONFIGURATION_PROVIDER, new ConfigurationSQLHelperDelegate());
        created.put(GENRE_PROVIDER, new GenreSQLHelperDelegate());
        created.put(MOVIE_PROVIDER, new MovieSQLHelperDelegate());
        created.put(MOVIE_GENRE_PROVIDER, new MovieGenreSQLHelperDelegate());
        created.put(PRODUCTION_COMPANY_PROVIDER, new ProductionCompanySQLHelperDelegate());
        created.put(PRODUCTION_COUNTRY_PROVIDER, new ProductionCountrySQLHelperDelegate());
        created.put(SPOKEN_LANGUAGE_PROVIDER, new SpokenLanguageSQLHelperDelegate());
        return created;
    }
}
