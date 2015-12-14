package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationSQLHelperDelegate;
import com.github.xavierlepretre.tmdb.model.movie.CollectionSQLHelperDelegate;
import com.github.xavierlepretre.tmdb.model.movie.GenreSQLHelperDelegate;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanySQLHelperDelegate;
import com.github.xavierlepretre.tmdb.model.production.ProductionCountrySQLHelperDelegate;

public class EntitySQLHelperDelegateFactory
{
    static final int COLLECTION_PROVIDER = 100;
    static final int CONFIGURATION_PROVIDER = 101;
    static final int GENRE_PROVIDER = 102;
    static final int PRODUCTION_COMPANY = 103;
    static final int PRODUCTION_COUNTRY = 104;

    @NonNull public SparseArray<EntitySQLHelperDelegate> createHelpers()
    {
        SparseArray<EntitySQLHelperDelegate> created = new SparseArray<>();
        created.put(COLLECTION_PROVIDER, new CollectionSQLHelperDelegate());
        created.put(CONFIGURATION_PROVIDER, new ConfigurationSQLHelperDelegate());
        created.put(GENRE_PROVIDER, new GenreSQLHelperDelegate());
        created.put(PRODUCTION_COMPANY, new ProductionCompanySQLHelperDelegate());
        created.put(PRODUCTION_COUNTRY, new ProductionCountrySQLHelperDelegate());
        return created;
    }
}
