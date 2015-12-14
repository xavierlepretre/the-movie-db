package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.EntitySQLHelperDelegate;

public class CollectionSQLHelperDelegate implements EntitySQLHelperDelegate
{
    @Override @NonNull public String getCreateQuery()
    {
        return "CREATE TABLE " + CollectionContract.TABLE_NAME + "("
                + CollectionContract.COLUMN_BACKDROP_PATH + " TEXT NULL,"
                + CollectionContract._ID + " INTEGER PRIMARY KEY NOT NULL,"
                + CollectionContract.COLUMN_NAME + " TEXT NULL,"
                + CollectionContract.COLUMN_POSTER_PATH + " TEXT NULL"
                + ");";
    }

    @NonNull @Override public String getUpgradeQuery(int oldVersion, int newVersion)
    {
        return "DROP TABLE IF EXISTS " + CollectionContract.TABLE_NAME + ";";
    }
}
