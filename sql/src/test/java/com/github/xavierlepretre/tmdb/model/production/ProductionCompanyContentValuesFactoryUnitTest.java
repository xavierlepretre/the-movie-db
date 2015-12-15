package com.github.xavierlepretre.tmdb.model.production;

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

public class ProductionCompanyContentValuesFactoryUnitTest
{
    private ProductionCompanyContentValuesFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = mock(ProductionCompanyContentValuesFactory.class);
    }

    @Test
    public void populate_works() throws Exception
    {
        ContentValues values = mock(ContentValues.class);
            ProductionCompanyDTO dto = new ProductionCompanyDTO(new ProductionCompanyId(5), "Columbia Pictures");
        doCallRealMethod().when(factory).populate(any(ContentValues.class), any(ProductionCompanyDTO.class));
        factory.populate(values, dto);
        verify(values).put(
                eq(ProductionCompanyContract._ID),
                eq(5));
        verify(values).put(
                eq(ProductionCompanyContract.COLUMN_NAME),
                eq("Columbia Pictures"));
    }

    @Test
    public void createSingle_callsPopulate() throws Exception
    {
        ProductionCompanyDTO dto = new ProductionCompanyDTO(new ProductionCompanyId(5), "Columbia Pictures");
        doCallRealMethod().when(factory).createFrom(any(ProductionCompanyDTO.class));
        factory.createFrom(dto);
        verify(factory).populate(notNull(ContentValues.class), eq(dto));
    }

    @Test
    public void createVectorFromCollection_callsSingle() throws Exception
    {
        ProductionCompanyDTO dto1 = new ProductionCompanyDTO(new ProductionCompanyId(5), "Columbia Pictures");
        ProductionCompanyDTO dto2 = new ProductionCompanyDTO(new ProductionCompanyId(10761), "Danjaq");
        doCallRealMethod().when(factory).createFrom(anyCollectionOf(ProductionCompanyDTO.class));
        factory.createFrom(Arrays.asList(dto1, dto2));
        verify(factory).createFrom(eq(dto1));
        verify(factory).createFrom(eq(dto2));
    }
}