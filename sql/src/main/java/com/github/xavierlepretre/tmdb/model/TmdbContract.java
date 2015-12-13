package com.github.xavierlepretre.tmdb.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract;
import com.github.xavierlepretre.tmdb.model.movie.CollectionContract;
import com.github.xavierlepretre.tmdb.model.movie.CollectionId;
import com.github.xavierlepretre.tmdb.model.movie.CollectionProviderDelegate;
import com.github.xavierlepretre.tmdb.model.movie.GenreContract;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;
import com.github.xavierlepretre.tmdb.model.movie.GenreProviderDelegate;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyContract;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyId;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyProviderDelegate;
import com.github.xavierlepretre.tmdb.model.production.ProductionCountryContract;
import com.github.xavierlepretre.tmdb.model.production.ProductionCountryProviderDelegate;
import com.neovisionaries.i18n.CountryCode;

public class TmdbContract
{
    public static final String CONTENT_AUTHORITY = "com.github.xavierlepretre.tmdb";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class CollectionEntity extends CollectionContract
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        @NonNull public static Uri buildUri(@NonNull CollectionId collectionId)
        {
            return CollectionProviderDelegate.buildCollectionLocation(CONTENT_URI, collectionId);
        }
    }

    public static final class ConfigurationEntity extends ConfigurationContract
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
    }

    public static final class GenreEntity extends GenreContract
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        @NonNull public static Uri buildUri(@NonNull GenreId genreId)
        {
            return GenreProviderDelegate.buildGenreLocation(CONTENT_URI, genreId);
        }
    }

    public static final class ProductionCompanyEntity extends ProductionCompanyContract
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        @NonNull public static Uri buildUri(@NonNull ProductionCompanyId productionCompanyId)
        {
            return ProductionCompanyProviderDelegate.buildProductionCompanyLocation(CONTENT_URI, productionCompanyId);
        }
    }

    public static final class ProductionCountryEntity extends ProductionCountryContract
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        @NonNull public static Uri buildUri(@NonNull CountryCode countryCode)
        {
            return ProductionCountryProviderDelegate.buildProductionCountryLocation(CONTENT_URI, countryCode);
        }
    }
}
