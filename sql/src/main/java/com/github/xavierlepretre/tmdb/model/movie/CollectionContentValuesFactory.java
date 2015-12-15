package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.*;

public class CollectionContentValuesFactory
{
    @NonNull public Vector<ContentValues> createFrom(@NonNull java.util.Collection<CollectionDTO> collectionDTOs)
    {
        Vector<ContentValues> values = new Vector<>(collectionDTOs.size());
        for (CollectionDTO collectionDTO : collectionDTOs)
        {
            values.add(createFrom(collectionDTO));
        }
        return values;
    }

    @NonNull public ContentValues createFrom(@NonNull CollectionDTO collectionDTO)
    {
        ContentValues contentValues = new ContentValues();
        populate(contentValues, collectionDTO);
        return contentValues;
    }

    public void populate(@NonNull ContentValues contentValues, @NonNull CollectionDTO collectionDTO)
    {
        contentValues.put(CollectionContract.COLUMN_BACKDROP_PATH, collectionDTO.getBackdropPath().getPath());
        contentValues.put(CollectionContract._ID, collectionDTO.getId().getId());
        contentValues.put(CollectionContract.COLUMN_NAME, collectionDTO.getName());
        contentValues.put(CollectionContract.COLUMN_POSTER_PATH, collectionDTO.getPosterPath().getPath());
    }
}
