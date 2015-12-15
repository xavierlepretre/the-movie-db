package com.github.xavierlepretre.tmdb.model.i18n;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Vector;

public class SpokenLanguageContentValuesFactory
{
    @NonNull public Vector<ContentValues> createFrom(@NonNull Collection<SpokenLanguageDTO> spokenLanguageDTOs)
    {
        Vector<ContentValues> values = new Vector<>(spokenLanguageDTOs.size());
        for (SpokenLanguageDTO spokenLanguageDTO : spokenLanguageDTOs)
        {
            values.add(createFrom(spokenLanguageDTO));
        }
        return values;
    }

    @NonNull public ContentValues createFrom(@NonNull SpokenLanguageDTO spokenLanguageDTO)
    {
        ContentValues contentValues = new ContentValues();
        populate(contentValues, spokenLanguageDTO);
        return contentValues;
    }

    public void populate(@NonNull ContentValues contentValues, @NonNull SpokenLanguageDTO spokenLanguageDTO)
    {
        contentValues.put(SpokenLanguageContract._ID, spokenLanguageDTO.getIso639Dash1().name());
        contentValues.put(SpokenLanguageContract.COLUMN_NAME, spokenLanguageDTO.getName());
    }
}
