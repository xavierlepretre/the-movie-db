package com.github.xavierlepretre.tmdb.sql.production;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.sql.EntitySQLiteOpenHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ProductionCountrySQLHelperDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.productionCountry.db";

    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new ProductionCountrySQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE productionCountry(_id CHARACTER(2) PRIMARY KEY NOT NULL,name TEXT NULL);");
    }

    @Test
    public void createRequest_compiles() throws Exception
    {
        SQLiteOpenHelper helper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new ProductionCountrySQLHelperDelegate());
        helper.getReadableDatabase().execSQL("SELECT * FROM productionCountry;");
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new ProductionCountrySQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS productionCountry;"
        );
    }
}