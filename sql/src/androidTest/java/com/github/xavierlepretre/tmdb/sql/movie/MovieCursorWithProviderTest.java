package com.github.xavierlepretre.tmdb.sql.movie;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.sql.EntitySQLiteOpenHelper;
import com.github.xavierlepretre.tmdb.sql.ParameterColumnValue;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.github.xavierlepretre.tmdb.model.movie.CollectionId;
import com.github.xavierlepretre.tmdb.model.movie.ImdbId;
import com.github.xavierlepretre.tmdb.model.movie.Movie;
import com.github.xavierlepretre.tmdb.model.movie.MovieContract;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(Parameterized.class)
public class MovieCursorWithProviderTest
{
    private static final String TEMP_DB_NAME = "temp.movie.db";
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{MovieContract.COLUMN_ADULT, "1"},
            new String[]{MovieContract.COLUMN_BACKDROP_PATH, "/path.jpg"},
            new String[]{MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, "98"},
            new String[]{MovieContract.COLUMN_BUDGET, "1000002"},
            new String[]{MovieContract._ID, "23"},
            new String[]{MovieContract.COLUMN_IMDB_ID, "5a91be"},
            new String[]{MovieContract.COLUMN_ORIGINAL_LANGUAGE, "en"},
            new String[]{MovieContract.COLUMN_RELEASE_DATE, "2011-12-20"}
    };

    private MovieProviderDelegate providerDelegate;
    private EntitySQLiteOpenHelper sqlHelper;
    private SimpleDateFormat formatter;

    @Parameterized.Parameter
    public ParameterColumnValue parameter;

    @Parameterized.Parameters()
    public static ParameterColumnValue[] getParameters()
    {
        return ParameterColumnValue.getPossibleParameters(POTENTIAL_COLUMNS);
    }

    @SuppressLint("SimpleDateFormat") @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = spy(new MovieProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/movie"),
                Uri.parse("content://content_authority/collection"),
                "dir_type",
                "item_type"));
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new MovieSQLHelperDelegate());

        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        for (String[] pair : POTENTIAL_COLUMNS)
        {
            values1.put(pair[0], pair[1]);
        }
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                new ContentValues[]{values1, values2});

        formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
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
        MovieCursor found = new MovieCursor(providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie"),
                parameter.columns.toArray(new String[parameter.columns.size()]),
                null, null, null, null,
                MovieContract._ID + " ASC", null));

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        Movie movie = found.getMovie();
        assertThat(movie.getAdult()).isEqualTo(
                (parameter.columns.contains(MovieContract.COLUMN_ADULT) || parameter.columns.size() == 0)
                        ? true
                        : null);
        assertThat(movie.getBackdropPath()).isEqualTo(
                (parameter.columns.contains(MovieContract.COLUMN_BACKDROP_PATH) || parameter.columns.size() == 0)
                        ? new ImagePath("/path.jpg")
                        : null);
        assertThat(movie.getBelongsToCollectionId()).isEqualTo(
                (parameter.columns.contains(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID) || parameter.columns.size() == 0)
                        ? new CollectionId(98)
                        : null);
        assertThat(movie.getBudget()).isEqualTo(
                (parameter.columns.contains(MovieContract.COLUMN_BUDGET) || parameter.columns.size() == 0)
                        ? 1000002L
                        : null);
        assertThat(movie.getId()).isEqualTo(
                (parameter.columns.contains(MovieContract._ID) || parameter.columns.size() == 0)
                        ? new MovieId(23)
                        : null);
        assertThat(movie.getImdbId()).isEqualTo(
                (parameter.columns.contains(MovieContract.COLUMN_IMDB_ID) || parameter.columns.size() == 0)
                        ? new ImdbId("5a91be")
                        : null);
        assertThat(movie.getOriginalLanguage()).isEqualTo(
                (parameter.columns.contains(MovieContract.COLUMN_ORIGINAL_LANGUAGE) || parameter.columns.size() == 0)
                        ? LanguageCode.en
                        : null);
        assertThat(movie.getReleaseDate()).isEqualTo(
                (parameter.columns.contains(MovieContract.COLUMN_RELEASE_DATE) || parameter.columns.size() == 0)
                        ? formatter.parse("2011-12-20")
                        : null);
    }
}
