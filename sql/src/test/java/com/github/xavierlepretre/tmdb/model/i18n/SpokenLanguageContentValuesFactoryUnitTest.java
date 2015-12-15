package com.github.xavierlepretre.tmdb.model.i18n;

import com.neovisionaries.i18n.LanguageCode;

import android.content.ContentValues;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SpokenLanguageContentValuesFactoryUnitTest
{
    private SpokenLanguageContentValuesFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = mock(SpokenLanguageContentValuesFactory.class);
    }

    @Test
    public void populate_works() throws Exception
    {
        ContentValues values = mock(ContentValues.class);
        SpokenLanguageDTO dto = new SpokenLanguageDTO(LanguageCode.en, "English");
        doCallRealMethod().when(factory).populate(any(ContentValues.class), any(SpokenLanguageDTO.class));
        factory.populate(values, dto);
        verify(values).put(
                eq(SpokenLanguageContract._ID),
                eq("en"));
        verify(values).put(
                eq(SpokenLanguageContract.COLUMN_NAME),
                eq("English"));
    }

    @Test
    public void createSingle_callsPopulate() throws Exception
    {
        SpokenLanguageDTO dto = new SpokenLanguageDTO(LanguageCode.en, "English");
        doCallRealMethod().when(factory).createFrom(any(SpokenLanguageDTO.class));
        factory.createFrom(dto);
        verify(factory).populate(notNull(ContentValues.class), eq(dto));
    }

    @Test
    public void createVectorFromCollection_callsSingle() throws Exception
    {
        SpokenLanguageDTO dto1 = new SpokenLanguageDTO(LanguageCode.en, "English");
        SpokenLanguageDTO dto2 = new SpokenLanguageDTO(LanguageCode.fr, "French");
        doCallRealMethod().when(factory).createFrom(anyCollectionOf(SpokenLanguageDTO.class));
        factory.createFrom(Arrays.asList(dto1, dto2));
        verify(factory).createFrom(eq(dto1));
        verify(factory).createFrom(eq(dto2));
    }
}