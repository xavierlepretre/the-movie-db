package com.github.xavierlepretre.tmdb.model.i18n;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class SpokenLanguageSQLHelperDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.spokenLanguage.db";

    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new SpokenLanguageSQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE spokenLanguage(_id CHARACTER(2) PRIMARY KEY NOT NULL,name TEXT NULL);");
    }

    @Test
    public void createRequest_compiles() throws Exception
    {
        SQLiteOpenHelper helper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new SpokenLanguageSQLHelperDelegate());
        helper.getReadableDatabase().execSQL("SELECT * FROM spokenLanguage;");
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new SpokenLanguageSQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS spokenLanguage;"
        );
    }
}