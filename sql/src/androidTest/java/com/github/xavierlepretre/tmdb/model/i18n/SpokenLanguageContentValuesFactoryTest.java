package com.github.xavierlepretre.tmdb.model.i18n;

import android.content.ContentValues;

import com.neovisionaries.i18n.LanguageCode;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Vector;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class SpokenLanguageContentValuesFactoryTest
{
    private SpokenLanguageContentValuesFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = spy(new SpokenLanguageContentValuesFactory());
    }

    @Test
    public void populate_works() throws Exception
    {
        ContentValues values = new ContentValues();
        SpokenLanguageDTO dto = new SpokenLanguageDTO(LanguageCode.en, "English");
        factory.populate(values, dto);
        assertThat(values.getAsString(SpokenLanguageContract._ID)).isEqualTo("en");
        assertThat(values.getAsString(SpokenLanguageContract.COLUMN_NAME)).isEqualTo("English");
    }

    @Test
    public void createSingle_works() throws Exception
    {
        SpokenLanguageDTO dto = new SpokenLanguageDTO(LanguageCode.en, "English");
        ContentValues values = factory.createFrom(dto);
        assertThat(values.getAsString(SpokenLanguageContract._ID)).isEqualTo("en");
        assertThat(values.getAsString(SpokenLanguageContract.COLUMN_NAME)).isEqualTo("English");
    }

    @Test
    public void createVectorFromCollection_callsSingle() throws Exception
    {
        SpokenLanguageDTO dto1 = new SpokenLanguageDTO(LanguageCode.en, "English");
        SpokenLanguageDTO dto2 = new SpokenLanguageDTO(LanguageCode.fr, "French");
        Vector<ContentValues> values = factory.createFrom(Arrays.asList(dto1, dto2));
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0).getAsString(SpokenLanguageContract._ID)).isEqualTo("en");
        assertThat(values.get(0).getAsString(SpokenLanguageContract.COLUMN_NAME)).isEqualTo("English");
        assertThat(values.get(1).getAsString(SpokenLanguageContract._ID)).isEqualTo("fr");
        assertThat(values.get(1).getAsString(SpokenLanguageContract.COLUMN_NAME)).isEqualTo("French");
    }
}