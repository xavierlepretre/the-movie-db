package com.github.xavierlepretre.tmdb.sql.movie;

import com.github.xavierlepretre.tmdb.model.movie.GenreId;
import com.github.xavierlepretre.tmdb.model.movie.MovieGenre;
import com.github.xavierlepretre.tmdb.model.movie.MovieGenreContract;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import android.database.MatrixCursor;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MovieGenreCursorTest
{
    private static final String[] COLUMNS = new String[]{
            MovieGenreContract.COLUMN_MOVIE_ID,
            MovieGenreContract.COLUMN_GENRE_ID
    };
    private static final String[] VALUES = new String[]{
            "23",
            "3"
    };

    @Test
    public void mayCreateGenre() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(VALUES);
        MovieGenreCursor entityCursor = new MovieGenreCursor(cursor);
        entityCursor.moveToFirst();

        MovieGenre genre = entityCursor.getMovieGenre();
        assertThat(genre.getMovieId()).isEqualTo(new MovieId(23));
        assertThat(genre.getGenreId()).isEqualTo(new GenreId(3));
    }

    @Test
    public void mayCreateGenreWithMissing() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(new String[0]);
        cursor.addRow(new String[0]);
        MovieGenreCursor entityCursor = new MovieGenreCursor(cursor);
        entityCursor.moveToFirst();

        MovieGenre genre = entityCursor.getMovieGenre();
        assertThat(genre.getMovieId()).isNull();
        assertThat(genre.getGenreId()).isNull();
    }

    @Test
    public void mayCreateGenreWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(new String[COLUMNS.length]);
        MovieGenreCursor entityCursor = new MovieGenreCursor(cursor);
        entityCursor.moveToFirst();

        MovieGenre genre = entityCursor.getMovieGenre();
        assertThat(genre.getMovieId()).isNull();
        assertThat(genre.getGenreId()).isNull();
    }
}