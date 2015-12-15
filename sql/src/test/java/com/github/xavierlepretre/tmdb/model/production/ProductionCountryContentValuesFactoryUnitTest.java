package com.github.xavierlepretre.tmdb.model.production;

import android.content.ContentValues;

import com.neovisionaries.i18n.CountryCode;

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

public class ProductionCountryContentValuesFactoryUnitTest
{
    private ProductionCountryContentValuesFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = mock(ProductionCountryContentValuesFactory.class);
    }

    @Test
    public void populate_works() throws Exception
    {
        ContentValues values = mock(ContentValues.class);
        ProductionCountryDTO dto = new ProductionCountryDTO(CountryCode.GB, "United Kingdom");
        doCallRealMethod().when(factory).populate(any(ContentValues.class), any(ProductionCountryDTO.class));
        factory.populate(values, dto);
        verify(values).put(
                eq(ProductionCountryContract._ID),
                eq("GB"));
        verify(values).put(
                eq(ProductionCountryContract.COLUMN_NAME),
                eq("United Kingdom"));
    }

    @Test
    public void createSingle_callsPopulate() throws Exception
    {
        ProductionCountryDTO dto = new ProductionCountryDTO(CountryCode.GB, "United Kingdom");
        doCallRealMethod().when(factory).createFrom(any(ProductionCountryDTO.class));
        factory.createFrom(dto);
        verify(factory).populate(notNull(ContentValues.class), eq(dto));
    }

    @Test
    public void createVectorFromCollection_callsSingle() throws Exception
    {
        ProductionCountryDTO dto1 = new ProductionCountryDTO(CountryCode.GB, "United Kingdom");
        ProductionCountryDTO dto2 = new ProductionCountryDTO(CountryCode.US, "United States of America");
        doCallRealMethod().when(factory).createFrom(anyCollectionOf(ProductionCountryDTO.class));
        factory.createFrom(Arrays.asList(dto1, dto2));
        verify(factory).createFrom(eq(dto1));
        verify(factory).createFrom(eq(dto2));
    }
}