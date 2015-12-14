package com.github.xavierlepretre.tmdb.model.movie;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MovieSQLHelperDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.movie.db";

    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(new MovieSQLHelperDelegate().getCreateQuery()).isEqualTo(
                "CREATE TABLE movie(" +
                        "adult INTEGER NULL," +
                        "backdropPath TEXT NULL," +
                        "belongsToCollectionId INTEGER NULL," +
                        "budget INTEGER NULL," +
                        "homepage TEXT NULL," +
                        "_id INTEGER PRIMARY KEY NOT NULL," +
                        "imdbId TEXT NULL," +
                        "originalLanguage CHARACTER(2) NULL," +
                        "originalTitle TEXT NULL," +
                        "overview TEXT NULL," +
                        "popularity REAL NULL," +
                        "posterPath TEXT NULL," +
                        "releaseDate TEXT NULL," +
                        "revenue INTEGER NULL," +
                        "runtime INTEGER NULL," +
                        "status TEXT NULL," +
                        "tagline TEXT NULL," +
                        "title TEXT NULL," +
                        "video INTEGER NULL," +
                        "voteAverage REAL NULL," +
                        "voteCount INTEGER NULL," +
                        "FOREIGN KEY (belongsToCollectionId) REFERENCES collection(_id));"
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
                new MovieSQLHelperDelegate());
        helper.getReadableDatabase().execSQL("SELECT * FROM movie;");
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(new MovieSQLHelperDelegate().getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS movie;"
        );
    }
}