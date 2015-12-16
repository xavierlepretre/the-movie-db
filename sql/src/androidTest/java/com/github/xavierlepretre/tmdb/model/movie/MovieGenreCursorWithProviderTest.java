package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;
import com.github.xavierlepretre.tmdb.model.ParameterColumnValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(Parameterized.class)
public class MovieGenreCursorWithProviderTest
{
    private static final String TEMP_DB_NAME = "temp.movieGenre.db";
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{MovieGenreContract.COLUMN_GENRE_ID, "3", "4"},
            new String[]{MovieGenreContract.COLUMN_MOVIE_ID, "1", "2"}
    };

    private MovieGenreProviderDelegate providerDelegate;
    private EntitySQLiteOpenHelper sqlHelper;

    @Parameterized.Parameter
    public ParameterColumnValue parameter;

    @Parameterized.Parameters()
    public static ParameterColumnValue[] getParameters()
    {
        return ParameterColumnValue.getPossibleParameters(POTENTIAL_COLUMNS);
    }

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = spy(new MovieGenreProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/movieGenre"),
                "movieGenre_dir_type",
                "movieGenre_item_type",
                Uri.parse("content://content_authority/genre"),
                "genre_dir_type",
                Uri.parse("content://content_authority/movie"),
                "movie_dir_type"));
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new MovieGenreSQLHelperDelegate());

        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        for (String[] pair : POTENTIAL_COLUMNS)
        {
            values1.put(pair[0], pair[1]);
            values2.put(pair[0], pair[2]);
        }
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{values1, values2});
    }

    @After
    public void tearDown() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
    }

    @Test
    public void queryListFromDb_isOk() throws Exception
    {
        //noinspection ConstantConditions
        MovieGenreCursor found = new MovieGenreCursor(providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                parameter.columns.toArray(new String[parameter.columns.size()]),
                null, null, null, null,
                MovieGenreContract.COLUMN_MOVIE_ID + " ASC", null));

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        MovieGenre genre = found.getMovieGenre();
        assertThat(genre.getGenreId()).isEqualTo(
                (parameter.columns.contains(MovieGenreContract.COLUMN_GENRE_ID) || parameter.columns.size() == 0)
                        ? new GenreId(3)
                        : null);
        assertThat(genre.getMovieId()).isEqualTo(
                (parameter.columns.contains(MovieGenreContract.COLUMN_MOVIE_ID) || parameter.columns.size() == 0)
                        ? new MovieId(1)
                        : null);
        assertThat(found.moveToNext()).isTrue();
        genre = found.getMovieGenre();
        assertThat(genre.getGenreId()).isEqualTo(
                (parameter.columns.contains(MovieGenreContract.COLUMN_GENRE_ID) || parameter.columns.size() == 0)
                        ? new GenreId(4)
                        : null);
        assertThat(genre.getMovieId()).isEqualTo(
                (parameter.columns.contains(MovieGenreContract.COLUMN_MOVIE_ID) || parameter.columns.size() == 0)
                        ? new MovieId(2)
                        : null);
        assertThat(found.moveToNext()).isFalse();
    }
}
