package com.github.xavierlepretre.tmdb.model.i18n;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.annotation.NonNull;

import com.neovisionaries.i18n.LanguageCode;

public class SpokenLanguageCursor extends CursorWrapper
{
    private final int idCol;
    private final int nameCol;

    public SpokenLanguageCursor(@NonNull Cursor cursor)
    {
        super(cursor);
        idCol = getColumnIndex(SpokenLanguageContract._ID);
        nameCol = getColumnIndex(SpokenLanguageContract.COLUMN_NAME);
    }

    @NonNull public SpokenLanguage getSpokenLanguage()
    {
        return new SpokenLanguage(
                idCol < 0 ? null : LanguageCode.getByCode(getString(idCol)),
                nameCol < 0 ? null : getString(nameCol));
    }
}
