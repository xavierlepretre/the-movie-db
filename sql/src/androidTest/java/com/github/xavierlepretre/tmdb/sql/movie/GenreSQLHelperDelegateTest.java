package com.github.xavierlepretre.tmdb.sql.movie;

import com.github.xavierlepretre.tmdb.sql.EntitySQLiteOpenHelper;

import org.junit.Test;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;

import static org.fest.assertions.api.Assertions.assertThat;

public class GenreSQLHelperDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.genre.db";

    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new GenreSQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE genre(_id INTEGER PRIMARY KEY NOT NULL,name TEXT NULL);"
        );
    }

    @Test
    public void createRequest_compiles() throws Exception
    {
        SQLiteOpenHelper helper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new GenreSQLHelperDelegate());
        helper.getReadableDatabase().execSQL("SELECT * FROM genre;");
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new GenreSQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS genre;"
        );
    }
}