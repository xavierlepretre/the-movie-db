package com.github.xavierlepretre.tmdb.model.production;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.EntitySQLHelperDelegate;

public class ProductionCompanySQLHelperDelegate implements EntitySQLHelperDelegate
{
    @Override @NonNull public String getCreateQuery()
    {
        return "CREATE TABLE " + ProductionCompanyContract.TABLE_NAME + "("
                + ProductionCompanyContract._ID + " INTEGER PRIMARY KEY NOT NULL,"
                + ProductionCompanyContract.COLUMN_NAME + " TEXT NULL"
                + ");";
    }

    @NonNull @Override public String getUpgradeQuery(int oldVersion, int newVersion)
    {
        return "DROP TABLE IF EXISTS " + ProductionCompanyContract.TABLE_NAME + ";";
    }
}
