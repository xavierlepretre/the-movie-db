package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.ParameterColumnValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(Parameterized.class)
public class GenreCursorWithProviderTest
{
    private static final String TEMP_DB_NAME = "temp.genre.db";
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{GenreContract._ID, "3", "4"},
            new String[]{GenreContract.COLUMN_NAME, "Adventure", "Comic"}
    };

    private GenreProviderDelegate providerDelegate;
    private GenreSQLiteOpenHelper sqlHelper;

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
        providerDelegate = spy(new GenreProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/genre"),
                "dir_type",
                "item_type"));
        sqlHelper = new GenreSQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1);

        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        for (String[] pair : POTENTIAL_COLUMNS)
        {
            values1.put(pair[0], pair[1]);
            values2.put(pair[0], pair[2]);
        }
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
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
        GenreCursor found = new GenreCursor(providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre"),
                parameter.columns.toArray(new String[parameter.columns.size()]),
                null, null, null, null,
                GenreContract._ID + " ASC", null));

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        Genre genre = found.getGenre();
        assertThat(genre.getId()).isEqualTo(
                (parameter.columns.contains(GenreContract._ID) || parameter.columns.size() == 0)
                        ? new GenreId(3)
                        : null);
        assertThat(genre.getName()).isEqualTo(
                (parameter.columns.contains(GenreContract.COLUMN_NAME) || parameter.columns.size() == 0)
                        ? "Adventure"
                        : null);
        assertThat(found.moveToNext()).isTrue();
        genre = found.getGenre();
        assertThat(genre.getId()).isEqualTo(
                (parameter.columns.contains(GenreContract._ID) || parameter.columns.size() == 0)
                        ? new GenreId(4)
                        : null);
        assertThat(genre.getName()).isEqualTo(
                (parameter.columns.contains(GenreContract.COLUMN_NAME) || parameter.columns.size() == 0)
                        ? "Comic"
                        : null);
        assertThat(found.moveToNext()).isFalse();
    }
}
