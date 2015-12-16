package com.github.xavierlepretre.tmdb.model.movie;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.annotation.NonNull;

public class MovieGenreCursor extends CursorWrapper
{
    private final int movieIdCol;
    private final int genreIdCol;

    public MovieGenreCursor(@NonNull Cursor cursor)
    {
        super(cursor);
        movieIdCol = getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID);
        genreIdCol = getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID);
    }

    @NonNull public MovieGenre getMovieGenre()
    {
        return new MovieGenre(
                (movieIdCol < 0 || isNull(movieIdCol)) ? null : new MovieId(getLong(movieIdCol)),
                (genreIdCol < 0 || isNull(genreIdCol)) ? null : new GenreId(getInt(genreIdCol)));
    }
}
