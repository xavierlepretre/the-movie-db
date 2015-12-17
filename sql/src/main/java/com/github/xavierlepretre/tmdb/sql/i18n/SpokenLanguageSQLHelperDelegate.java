package com.github.xavierlepretre.tmdb.sql.i18n;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.i18n.SpokenLanguageContract;
import com.github.xavierlepretre.tmdb.sql.EntitySQLHelperDelegate;

public class SpokenLanguageSQLHelperDelegate implements EntitySQLHelperDelegate
{
    @Override @NonNull public String getCreateQuery()
    {
        return "CREATE TABLE " + SpokenLanguageContract.TABLE_NAME + "("
                + SpokenLanguageContract._ID + " CHARACTER(2) PRIMARY KEY NOT NULL,"
                + SpokenLanguageContract.COLUMN_NAME + " TEXT NULL"
                + ");";
    }

    @NonNull @Override public String getUpgradeQuery(int oldVersion, int newVersion)
    {
        return "DROP TABLE IF EXISTS " + SpokenLanguageContract.TABLE_NAME + ";";
    }
}
