package com.github.xavierlepretre.tmdb.sql.production;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyContract;
import com.github.xavierlepretre.tmdb.sql.EntitySQLHelperDelegate;

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
