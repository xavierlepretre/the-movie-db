package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.EntitySQLHelperDelegate;

public class GenreSQLHelperDelegate implements EntitySQLHelperDelegate
{
    @Override @NonNull public String getCreateQuery()
    {
        return "CREATE TABLE " + GenreContract.TABLE_NAME + "("
                + GenreContract._ID + " INTEGER PRIMARY KEY NOT NULL,"
                + GenreContract.COLUMN_NAME + " TEXT NULL"
                + ");";
    }

    @NonNull @Override public String getUpgradeQuery(int oldVersion, int newVersion)
    {
        return "DROP TABLE IF EXISTS " + GenreContract.TABLE_NAME + ";";
    }
}
