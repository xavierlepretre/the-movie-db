package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.EntitySQLHelperDelegate;

public class MovieGenreSQLHelperDelegate implements EntitySQLHelperDelegate
{
    @Override @NonNull public String getCreateQuery()
    {
        return "CREATE TABLE " + MovieGenreContract.TABLE_NAME + "("
                + MovieGenreContract.COLUMN_MOVIE_ID + " INTEGER NOT NULL,"
                + MovieGenreContract.COLUMN_GENRE_ID + " INTEGER NOT NULL,"
                + "PRIMARY KEY (" + MovieGenreContract.COLUMN_MOVIE_ID + "," + MovieGenreContract.COLUMN_GENRE_ID + "),"
                + "FOREIGN KEY (" + MovieGenreContract.COLUMN_MOVIE_ID + ") REFERENCES "
                + MovieContract.TABLE_NAME + "(" + MovieContract._ID + "),"
                + "FOREIGN KEY (" + MovieGenreContract.COLUMN_GENRE_ID + ") REFERENCES "
                + GenreContract.TABLE_NAME + "(" + GenreContract._ID + ")"
                + ");";
    }

    @NonNull @Override public String getUpgradeQuery(int oldVersion, int newVersion)
    {
        return "DROP TABLE IF EXISTS " + MovieGenreContract.TABLE_NAME + ";";
    }
}
