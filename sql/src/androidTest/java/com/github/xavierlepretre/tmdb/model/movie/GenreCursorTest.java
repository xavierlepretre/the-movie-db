package com.github.xavierlepretre.tmdb.model.movie;

import android.database.MatrixCursor;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class GenreCursorTest
{
    private static final String[] COLUMNS = new String[]{
            GenreContract._ID,
            GenreContract.COLUMN_NAME
    };
    private static final String[] VALUES = new String[]{
            "23",
            "Action"
    };

    @Test
    public void mayCreateGenre() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(VALUES);
        GenreCursor entityCursor = new GenreCursor(cursor);
        entityCursor.moveToFirst();

        Genre genre = entityCursor.getGenre();
        assertThat(genre.getId()).isEqualTo(new GenreId(23));
        assertThat(genre.getName()).isEqualTo("Action");
    }

    @Test
    public void mayCreateGenreWithMissing() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(new String[0]);
        cursor.addRow(new String[0]);
        GenreCursor entityCursor = new GenreCursor(cursor);
        entityCursor.moveToFirst();

        Genre genre = entityCursor.getGenre();
        assertThat(genre.getId()).isNull();
        assertThat(genre.getName()).isNull();
    }

    @Test
    public void mayCreateGenreWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(new String[COLUMNS.length]);
        GenreCursor entityCursor = new GenreCursor(cursor);
        entityCursor.moveToFirst();

        Genre genre = entityCursor.getGenre();
        assertThat(genre.getId()).isNull();
        assertThat(genre.getName()).isNull();
    }
}