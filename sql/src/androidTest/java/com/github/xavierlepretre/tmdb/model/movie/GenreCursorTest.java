package com.github.xavierlepretre.tmdb.model.movie;

import android.database.Cursor;
import android.support.annotation.Nullable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class GenreCursorTest
{
    private static final int DESIRED_ID = 23;
    private static final String DESIRED_NAME = "Action";

    private static class Parameter
    {
        private final int idCol;
        @Nullable private final GenreId genreId;
        private final int nameCol;
        @Nullable private final String name;

        public Parameter(int idCol,
                         @Nullable GenreId genreId,
                         int nameCol,
                         @Nullable String name)
        {
            this.idCol = idCol;
            this.genreId = genreId;
            this.nameCol = nameCol;
            this.name = name;
        }
    }

    @Parameterized.Parameter
    public Parameter parameter;

    @Parameterized.Parameters()
    public static Parameter[] getParameters()
    {
        return new Parameter[]{
                new Parameter(-1, null, -1, null),
                new Parameter(-1, null, 1, DESIRED_NAME),
                new Parameter(0, new GenreId(DESIRED_ID), -1, null),
                new Parameter(0, new GenreId(DESIRED_ID), 1, DESIRED_NAME)
        };
    }

    @Test
    public void mayCreateGenreWithNulls() throws Exception
    {
        Cursor baseCursor = mock(Cursor.class);
        when(baseCursor.getColumnIndex(anyString())).thenReturn(2);
        when(baseCursor.getColumnIndex(eq(GenreContract._ID))).thenReturn(parameter.idCol);
        when(baseCursor.getColumnIndex(eq(GenreContract.COLUMN_NAME))).thenReturn(parameter.nameCol);
        when(baseCursor.getInt(0)).thenReturn(DESIRED_ID);
        when(baseCursor.getString(1)).thenReturn(DESIRED_NAME);
        GenreCursor cursor = new GenreCursor(baseCursor);

        Genre genre = cursor.getGenre();
        assertThat(genre.getId()).isEqualTo(parameter.genreId);
        assertThat(genre.getName()).isEqualTo(parameter.name);
    }
}