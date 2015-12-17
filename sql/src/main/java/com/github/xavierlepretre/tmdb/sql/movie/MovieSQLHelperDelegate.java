package com.github.xavierlepretre.tmdb.sql.movie;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.movie.CollectionContract;
import com.github.xavierlepretre.tmdb.model.movie.MovieContract;
import com.github.xavierlepretre.tmdb.sql.EntitySQLHelperDelegate;

public class MovieSQLHelperDelegate implements EntitySQLHelperDelegate
{
    @Override @NonNull public String getCreateQuery()
    {
        return "CREATE TABLE " + MovieContract.TABLE_NAME + "("
                + MovieContract.COLUMN_ADULT + " INTEGER NULL,"
                + MovieContract.COLUMN_BACKDROP_PATH + " TEXT NULL,"
                + MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID + " INTEGER NULL,"
                + MovieContract.COLUMN_BUDGET + " INTEGER NULL,"
                + MovieContract.COLUMN_HOMEPAGE + " TEXT NULL,"
                + MovieContract._ID + " INTEGER PRIMARY KEY NOT NULL,"
                + MovieContract.COLUMN_IMDB_ID + " TEXT NULL,"
                + MovieContract.COLUMN_ORIGINAL_LANGUAGE + " CHARACTER(2) NULL,"
                + MovieContract.COLUMN_ORIGINAL_TITLE + " TEXT NULL,"
                + MovieContract.COLUMN_OVERVIEW + " TEXT NULL,"
                + MovieContract.COLUMN_POPULARITY + " REAL NULL,"
                + MovieContract.COLUMN_POSTER_PATH + " TEXT NULL,"
                + MovieContract.COLUMN_RELEASE_DATE + " TEXT NULL,"
                + MovieContract.COLUMN_REVENUE + " INTEGER NULL,"
                + MovieContract.COLUMN_RUNTIME + " INTEGER NULL,"
                + MovieContract.COLUMN_STATUS + " TEXT NULL,"
                + MovieContract.COLUMN_TAGLINE + " TEXT NULL,"
                + MovieContract.COLUMN_TITLE + " TEXT NULL,"
                + MovieContract.COLUMN_VIDEO + " INTEGER NULL,"
                + MovieContract.COLUMN_VOTE_AVERAGE + " REAL NULL,"
                + MovieContract.COLUMN_VOTE_COUNT + " INTEGER NULL,"
                + "FOREIGN KEY (" + MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID + ") REFERENCES "
                + CollectionContract.TABLE_NAME + "(" + CollectionContract._ID +")"
                + ");";
    }

    @NonNull @Override public String getUpgradeQuery(int oldVersion, int newVersion)
    {
        return "DROP TABLE IF EXISTS " + MovieContract.TABLE_NAME + ";";
    }
}
