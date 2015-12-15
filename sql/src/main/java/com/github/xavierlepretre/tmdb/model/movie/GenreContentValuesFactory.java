package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Vector;

public class GenreContentValuesFactory
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
        populate(contentValues, genreDTO.getId());
        contentValues.put(GenreContract.COLUMN_NAME, genreDTO.getName());
    }

    @NonNull public Vector<ContentValues> createFrom(
            @NonNull Collection<GenreId> genreIds,
            @SuppressWarnings("UnusedParameters") @Nullable GenreId typeQualifier)
    {
        Vector<ContentValues> values = new Vector<>(genreIds.size());
        for (GenreId genreId : genreIds)
        {
            values.add(createFrom(genreId));
        }
        return values;
    }

    @NonNull public ContentValues createFrom(@NonNull GenreId genreId)
    {
        ContentValues contentValues = new ContentValues();
        populate(contentValues, genreId);
        return contentValues;
    }

    public void populate(@NonNull ContentValues contentValues, @NonNull GenreId genreId)
    {
        contentValues.put(GenreContract._ID, genreId.getId());
    }
}
