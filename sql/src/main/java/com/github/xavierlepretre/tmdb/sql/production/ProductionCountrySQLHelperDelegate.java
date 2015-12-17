package com.github.xavierlepretre.tmdb.sql.production;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.production.ProductionCountryContract;
import com.github.xavierlepretre.tmdb.sql.EntitySQLHelperDelegate;

public class ProductionCountrySQLHelperDelegate implements EntitySQLHelperDelegate
{
    @Override @NonNull public String getCreateQuery()
    {
        return "CREATE TABLE " + ProductionCountryContract.TABLE_NAME + "("
                + ProductionCountryContract._ID + " CHARACTER(2) PRIMARY KEY NOT NULL,"
                + ProductionCountryContract.COLUMN_NAME + " TEXT NULL"
                + ");";
    }

    @NonNull @Override public String getUpgradeQuery(int oldVersion, int newVersion)
    {
        return "DROP TABLE IF EXISTS " + ProductionCountryContract.TABLE_NAME + ";";
    }
}
