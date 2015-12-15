package com.github.xavierlepretre.tmdb.model.production;

import android.content.ContentValues;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Vector;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class ProductionCompanyContentValuesFactoryTest
{
    private ProductionCompanyContentValuesFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = spy(new ProductionCompanyContentValuesFactory());
    }

    @Test
    public void populate_works() throws Exception
    {
        ContentValues values = new ContentValues();
        ProductionCompanyDTO dto = new ProductionCompanyDTO(new ProductionCompanyId(5), "Columbia Pictures");
        factory.populate(values, dto);
        assertThat(values.getAsInteger(ProductionCompanyContract._ID)).isEqualTo(5);
        assertThat(values.getAsString(ProductionCompanyContract.COLUMN_NAME)).isEqualTo("Columbia Pictures");
    }

    @Test
    public void createSingle_works() throws Exception
    {
        ProductionCompanyDTO dto = new ProductionCompanyDTO(new ProductionCompanyId(5), "Columbia Pictures");
        ContentValues values = factory.createFrom(dto);
        assertThat(values.getAsInteger(ProductionCompanyContract._ID)).isEqualTo(5);
        assertThat(values.getAsString(ProductionCompanyContract.COLUMN_NAME)).isEqualTo("Columbia Pictures");
    }

    @Test
    public void createVectorFromCollection_callsSingle() throws Exception
    {
        ProductionCompanyDTO dto1 = new ProductionCompanyDTO(new ProductionCompanyId(5), "Columbia Pictures");
        ProductionCompanyDTO dto2 = new ProductionCompanyDTO(new ProductionCompanyId(10761), "Danjaq");
        Vector<ContentValues> values = factory.createFrom(Arrays.asList(dto1, dto2));
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0).getAsInteger(ProductionCompanyContract._ID)).isEqualTo(5);
        assertThat(values.get(0).getAsString(ProductionCompanyContract.COLUMN_NAME)).isEqualTo("Columbia Pictures");
        assertThat(values.get(1).getAsInteger(ProductionCompanyContract._ID)).isEqualTo(10761);
        assertThat(values.get(1).getAsString(ProductionCompanyContract.COLUMN_NAME)).isEqualTo("Danjaq");
    }
}