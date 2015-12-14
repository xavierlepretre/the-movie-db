package com.github.xavierlepretre.tmdb.model.conf;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ConfigurationSQLHelperDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.configuration.db";

    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new ConfigurationSQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE configuration(_id INTEGER PRIMARY KEY NOT NULL," +
                        "imagesBaseUrl TEXT NULL," +
                        "imagesSecureBaseUrl TEXT NULL," +
                        "imagesBackdropSizes TEXT NULL," +
                        "imagesLogoSizes TEXT NULL," +
                        "imagesPosterSizes TEXT NULL," +
                        "imagesProfileSizes TEXT NULL," +
                        "imagesStillSizes TEXT NULL," +
                        "changeKeys TEXT NULL);"
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
                new ConfigurationSQLHelperDelegate());
        helper.getReadableDatabase().execSQL("SELECT * FROM configuration;");
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new ConfigurationSQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS configuration;"
        );
    }
}