package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

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
    private static final GenreId DESIRED_ID_1 = new GenreId(3);
    private static final String DESIRED_NAME_1 = "Adventure";
    private static final GenreId DESIRED_ID_2 = new GenreId(4);
    private static final String DESIRED_NAME_2 = "Comic";

    private GenreProviderDelegate providerDelegate;
    private GenreSQLiteOpenHelper sqlHelper;

    private static class Parameter
    {
        @Nullable private final String[] projection;
        @Nullable private final GenreId genreId1;
        @Nullable private final String name1;
        @Nullable private final GenreId genreId2;
        @Nullable private final String name2;

        public Parameter(@Nullable String[] projection,
                         @Nullable GenreId genreId1,
                         @Nullable String name1,
                         @Nullable GenreId genreId2,
                         @Nullable String name2)
        {
            this.projection = projection;
            this.genreId1 = genreId1;
            this.name1 = name1;
            this.genreId2 = genreId2;
            this.name2 = name2;
        }
    }

    @Parameterized.Parameter
    public Parameter parameter;

    @Parameterized.Parameters()
    public static Parameter[] getParameters()
    {
        return new Parameter[]
                {
                        new Parameter(null,
                                DESIRED_ID_1,
                                DESIRED_NAME_1,
                                DESIRED_ID_2,
                                DESIRED_NAME_2),
                        new Parameter(new String[0],
                                DESIRED_ID_1,
                                DESIRED_NAME_1,
                                DESIRED_ID_2,
                                DESIRED_NAME_2),
                        new Parameter(new String[]{GenreContract._ID},
                                DESIRED_ID_1,
                                null,
                                DESIRED_ID_2,
                                null),
                        new Parameter(new String[]{GenreContract.COLUMN_NAME},
                                null,
                                DESIRED_NAME_1,
                                null,
                                DESIRED_NAME_2),
                        new Parameter(new String[]{GenreContract._ID, GenreContract.COLUMN_NAME},
                                DESIRED_ID_1,
                                DESIRED_NAME_1,
                                DESIRED_ID_2,
                                DESIRED_NAME_2),
                        new Parameter(new String[]{GenreContract.COLUMN_NAME, GenreContract._ID},
                                DESIRED_ID_1,
                                DESIRED_NAME_1,
                                DESIRED_ID_2,
                                DESIRED_NAME_2),
                };
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
        values1.put(GenreContract._ID, DESIRED_ID_1.getId());
        values1.put(GenreContract.COLUMN_NAME, DESIRED_NAME_1);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(GenreContract._ID, DESIRED_ID_2.getId());
        values2.put(GenreContract.COLUMN_NAME, DESIRED_NAME_2);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values2);
    }

    @After
    public void tearDown() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
    }

    @Test
    public void queryListFromDb_isOk() throws Exception
    {
        GenreCursor found = new GenreCursor(providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre"),
                parameter.projection,
                null, null, null, null,
                GenreContract._ID + " ASC", null));

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        Genre genre = found.getGenre();
        assertThat(genre.getId()).isEqualTo(parameter.genreId1);
        assertThat(genre.getName()).isEqualTo(parameter.name1);
        assertThat(found.moveToNext()).isTrue();
        genre = found.getGenre();
        assertThat(genre.getId()).isEqualTo(parameter.genreId2);
        assertThat(genre.getName()).isEqualTo(parameter.name2);
        assertThat(found.moveToNext()).isFalse();
    }
}
