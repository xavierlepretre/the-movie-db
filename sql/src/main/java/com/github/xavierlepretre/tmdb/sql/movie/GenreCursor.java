package com.github.xavierlepretre.tmdb.sql.movie;

import com.github.xavierlepretre.tmdb.model.movie.Genre;
import com.github.xavierlepretre.tmdb.model.movie.GenreContract;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.annotation.NonNull;

public class GenreCursor extends CursorWrapper
{
    private final int idCol;
    private final int nameCol;

    public GenreCursor(@NonNull Cursor cursor)
    {
        super(cursor);
        idCol = getColumnIndex(GenreContract._ID);
        nameCol = getColumnIndex(GenreContract.COLUMN_NAME);
    }

    @NonNull public Genre getGenre()
    {
        return new Genre(
                (idCol < 0 || isNull(idCol)) ? null : new GenreId(getInt(idCol)),
                (nameCol < 0 || isNull(nameCol)) ? null : getString(nameCol));
    }
}
