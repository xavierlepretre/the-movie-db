package com.github.xavierlepretre.tmdb.model.movie;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class CollectionSQLHelperDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.collection.db";

    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new CollectionSQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE collection(backdropPath TEXT NULL,_id INTEGER PRIMARY KEY NOT NULL,name TEXT NULL,posterPath TEXT NULL);"
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
                new CollectionSQLHelperDelegate());
        helper.getReadableDatabase().execSQL("SELECT * FROM collection;");
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new CollectionSQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS collection;"
        );
    }
}