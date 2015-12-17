package com.github.xavierlepretre.tmdb.sql.i18n;

import android.content.ContentValues;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.sql.EntitySQLiteOpenHelper;
import com.github.xavierlepretre.tmdb.sql.ParameterColumnValue;
import com.github.xavierlepretre.tmdb.model.i18n.SpokenLanguage;
import com.github.xavierlepretre.tmdb.model.i18n.SpokenLanguageContract;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(Parameterized.class)
public class SpokenLanguageCursorWithProviderTest
{
    private static final String TEMP_DB_NAME = "temp.spokenLanguage.db";
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{SpokenLanguageContract._ID, "en", "fr"},
            new String[]{SpokenLanguageContract.COLUMN_NAME, "English", "French"}
    };

    private SpokenLanguageProviderDelegate providerDelegate;
    private EntitySQLiteOpenHelper sqlHelper;

    @Parameterized.Parameter
    public ParameterColumnValue parameter;

    @Parameterized.Parameters()
    public static ParameterColumnValue[] getParameters()
    {
        return ParameterColumnValue.getPossibleParameters(POTENTIAL_COLUMNS);
    }

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = spy(new SpokenLanguageProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/spokenLanguage"),
                "dir_type",
                "item_type"));
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new SpokenLanguageSQLHelperDelegate());

        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        for (String[] pair : POTENTIAL_COLUMNS)
        {
            values1.put(pair[0], pair[1]);
            values2.put(pair[0], pair[2]);
        }
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                new ContentValues[]{values1, values2});
    }

    @After
    public void tearDown() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
    }

    @Test
    public void queryListFromDb_isOk() throws Exception
    {
        //noinspection ConstantConditions
        SpokenLanguageCursor found = new SpokenLanguageCursor(providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                parameter.columns.toArray(new String[parameter.columns.size()]),
                null, null, null, null,
                SpokenLanguageContract._ID + " ASC", null));

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        SpokenLanguage spokenLanguage = found.getSpokenLanguage();
        assertThat(spokenLanguage.getIso639Dash1()).isEqualTo(
                (parameter.columns.contains(SpokenLanguageContract._ID) || parameter.columns.size() == 0)
                        ? LanguageCode.en
                        : null);
        assertThat(spokenLanguage.getName()).isEqualTo(
                (parameter.columns.contains(SpokenLanguageContract.COLUMN_NAME) || parameter.columns.size() == 0)
                        ? "English"
                        : null);
        assertThat(found.moveToNext()).isTrue();
        spokenLanguage = found.getSpokenLanguage();
        assertThat(spokenLanguage.getIso639Dash1()).isEqualTo(
                (parameter.columns.contains(SpokenLanguageContract._ID) || parameter.columns.size() == 0)
                        ? LanguageCode.fr
                        : null);
        assertThat(spokenLanguage.getName()).isEqualTo(
                (parameter.columns.contains(SpokenLanguageContract.COLUMN_NAME) || parameter.columns.size() == 0)
                        ? "French"
                        : null);
        assertThat(found.moveToNext()).isFalse();
    }
}
