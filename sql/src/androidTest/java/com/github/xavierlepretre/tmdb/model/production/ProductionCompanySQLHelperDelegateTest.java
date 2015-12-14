package com.github.xavierlepretre.tmdb.model.production;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ProductionCompanySQLHelperDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.productionCompany.db";

    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new ProductionCompanySQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE productionCompany(_id INTEGER PRIMARY KEY NOT NULL,name TEXT NULL);");
    }

    @Test
    public void createRequest_compiles() throws Exception
    {
        SQLiteOpenHelper helper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new ProductionCompanySQLHelperDelegate());
        helper.getReadableDatabase().execSQL("SELECT * FROM productionCompany;");
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new ProductionCompanySQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS productionCompany;"
        );
    }
}