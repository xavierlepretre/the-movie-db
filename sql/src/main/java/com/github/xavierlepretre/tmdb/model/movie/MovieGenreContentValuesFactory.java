package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.Vector;

public class MovieGenreContentValuesFactory
{
    @NonNull public Vector<ContentValues> createFrom(@NonNull MovieShortDTO movieDTO)
    {
        Vector<ContentValues> contentValues = new Vector<>(movieDTO.getGenreIds().size());
        for (GenreId genreId : movieDTO.getGenreIds())
        {
            ContentValues value = new ContentValues();
            value.put(MovieGenreContract.COLUMN_MOVIE_ID, movieDTO.getId().getId());
            value.put(MovieGenreContract.COLUMN_GENRE_ID, genreId.getId());
            contentValues.add(value);
        }
        return contentValues;
    }
}
