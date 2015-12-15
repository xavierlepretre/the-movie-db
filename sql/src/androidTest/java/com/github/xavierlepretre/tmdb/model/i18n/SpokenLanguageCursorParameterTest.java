package com.github.xavierlepretre.tmdb.model.i18n;

import android.database.MatrixCursor;

import com.github.xavierlepretre.tmdb.model.ParameterColumnValue;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class SpokenLanguageCursorParameterTest
{
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{SpokenLanguageContract._ID, "en"},
            new String[]{SpokenLanguageContract.COLUMN_NAME, "English"}
    };

    @Parameterized.Parameter
    public ParameterColumnValue parameter;

    @Parameterized.Parameters()
    public static ParameterColumnValue[] getParameters()
    {
        return ParameterColumnValue.getPossibleParameters(POTENTIAL_COLUMNS);
    }

    @Test
    public void mayCreateSpokenLanguageWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(parameter.columns.toArray(new String[parameter.columns.size()]));
        for (List<String> row : parameter.rows)
        {
            cursor.addRow(row);
        }
        SpokenLanguageCursor entityCursor = new SpokenLanguageCursor(cursor);
        entityCursor.moveToFirst();

        SpokenLanguage spokenLanguage = entityCursor.getSpokenLanguage();
        assertThat(spokenLanguage.getIso639Dash1()).isEqualTo(
                parameter.columns.contains(SpokenLanguageContract._ID)
                        ? LanguageCode.en
                        : null);
        assertThat(spokenLanguage.getName()).isEqualTo(
                parameter.columns.contains(SpokenLanguageContract.COLUMN_NAME)
                        ? "English"
                        : null);
    }
}