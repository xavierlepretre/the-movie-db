package com.github.xavierlepretre.tmdb.model.movie;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MovieGenreSQLHelperDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.movieGenre.db";

    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new MovieGenreSQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE movieGenre(" +
                        "movieId INTEGER NOT NULL," +
                        "genreId INTEGER NOT NULL," +
                        "PRIMARY KEY (movieId,genreId)," +
                        "FOREIGN KEY (movieId) REFERENCES movie(_id)," +
                        "FOREIGN KEY (genreId) REFERENCES genre(_id));"
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
                new MovieGenreSQLHelperDelegate());
        helper.getReadableDatabase().execSQL("SELECT * FROM movieGenre;");
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new MovieGenreSQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS movieGenre;"
        );
    }
}