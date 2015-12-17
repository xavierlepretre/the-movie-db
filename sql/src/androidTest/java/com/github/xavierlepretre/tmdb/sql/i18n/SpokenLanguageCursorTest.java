package com.github.xavierlepretre.tmdb.sql.i18n;

import android.database.MatrixCursor;

import com.github.xavierlepretre.tmdb.model.i18n.SpokenLanguage;
import com.github.xavierlepretre.tmdb.model.i18n.SpokenLanguageContract;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class SpokenLanguageCursorTest
{
    private static final String[] COLUMNS = new String[]{
            SpokenLanguageContract._ID,
            SpokenLanguageContract.COLUMN_NAME
    };
    private static final String[] VALUES = new String[]{
            "en",
            "English"
    };

    @Test
    public void mayCreateSpokenLanguage() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(VALUES);
        SpokenLanguageCursor entityCursor = new SpokenLanguageCursor(cursor);
        entityCursor.moveToFirst();

        SpokenLanguage spokenLanguage = entityCursor.getSpokenLanguage();
        assertThat(spokenLanguage.getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(spokenLanguage.getName()).isEqualTo("English");
    }

    @Test
    public void mayCreateSpokenLanguageWithMissing() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(new String[0]);
        cursor.addRow(new String[0]);
        SpokenLanguageCursor entityCursor = new SpokenLanguageCursor(cursor);
        entityCursor.moveToFirst();

        SpokenLanguage spokenLanguage = entityCursor.getSpokenLanguage();
        assertThat(spokenLanguage.getIso639Dash1()).isNull();
        assertThat(spokenLanguage.getName()).isNull();
    }

    @Test
    public void mayCreateSpokenLanguageWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(new String[COLUMNS.length]);
        SpokenLanguageCursor entityCursor = new SpokenLanguageCursor(cursor);
        entityCursor.moveToFirst();

        SpokenLanguage spokenLanguage = entityCursor.getSpokenLanguage();
        assertThat(spokenLanguage.getIso639Dash1()).isNull();
        assertThat(spokenLanguage.getName()).isNull();
    }
}