package com.github.xavierlepretre.tmdb.sql.i18n;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.i18n.SpokenLanguage;
import com.github.xavierlepretre.tmdb.model.i18n.SpokenLanguageContract;
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
                (idCol < 0 || isNull(idCol)) ? null : LanguageCode.getByCode(getString(idCol)),
                nameCol < 0 ? null : getString(nameCol));
    }
}
