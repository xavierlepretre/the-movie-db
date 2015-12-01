package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Vector;

public class ContentValuesGenreFactory
{
    @NonNull public Vector<ContentValues> createFrom(@NonNull GenreListDTO genreDTOs)
    {
        return createFrom(genreDTOs.getGenres());
    }

    @NonNull public Vector<ContentValues> createFrom(@NonNull Collection<GenreDTO> genreDTOs)
    {
        Vector<ContentValues> values = new Vector<>(genreDTOs.size());
        for (GenreDTO genreDTO : genreDTOs)
        {
            values.add(createFrom(genreDTO));
        }
        return values;
    }

    @NonNull public ContentValues createFrom(@NonNull GenreDTO genreDTO)
    {
        ContentValues contentValues = new ContentValues();
        populate(contentValues, genreDTO);
        return contentValues;
    }

    public void populate(@NonNull ContentValues contentValues, @NonNull GenreDTO genreDTO)
    {
        contentValues.put(GenreContract._ID, genreDTO.getId().getId());
        contentValues.put(GenreContract.COLUMN_NAME, genreDTO.getName());
    }
}
