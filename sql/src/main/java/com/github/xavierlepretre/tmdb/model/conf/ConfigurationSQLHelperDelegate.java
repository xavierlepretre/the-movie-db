package com.github.xavierlepretre.tmdb.model.conf;

import com.github.xavierlepretre.tmdb.model.EntitySQLHelperDelegate;

import android.support.annotation.NonNull;

public class ConfigurationSQLHelperDelegate implements EntitySQLHelperDelegate
{
    @Override @NonNull public String getCreateQuery()
    {
        return "CREATE TABLE " + ConfigurationContract.TABLE_NAME + "("
                + ConfigurationContract._ID + " INTEGER PRIMARY KEY NOT NULL,"
                + ConfigurationContract.ImagesConfSegment.COLUMN_BASE_URL + " TEXT NULL,"
                + ConfigurationContract.ImagesConfSegment.COLUMN_SECURE_BASE_URL + " TEXT NULL,"
                + ConfigurationContract.ImagesConfSegment.COLUMN_BACKDROP_SIZES + " TEXT NULL,"
                + ConfigurationContract.ImagesConfSegment.COLUMN_LOGO_SIZES + " TEXT NULL,"
                + ConfigurationContract.ImagesConfSegment.COLUMN_POSTER_SIZES + " TEXT NULL,"
                + ConfigurationContract.ImagesConfSegment.COLUMN_PROFILE_SIZES + " TEXT NULL,"
                + ConfigurationContract.ImagesConfSegment.COLUMN_STILL_SIZES + " TEXT NULL,"
                + ConfigurationContract.COLUMN_CHANGE_KEYS + " TEXT NULL"
                + ");";
    }

    @NonNull @Override public String getUpgradeQuery(int oldVersion, int newVersion)
    {
        return "DROP TABLE IF EXISTS " + ConfigurationContract.TABLE_NAME + ";";
    }
}
