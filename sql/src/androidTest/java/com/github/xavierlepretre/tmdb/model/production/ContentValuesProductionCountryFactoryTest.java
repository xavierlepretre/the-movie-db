package com.github.xavierlepretre.tmdb.model.production;

import android.content.ContentValues;

import com.neovisionaries.i18n.CountryCode;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Vector;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class ContentValuesProductionCountryFactoryTest
{
    private ContentValuesProductionCountryFactory factory;

    @Before
    public void setUp() throws Exception
    {
        factory = spy(new ContentValuesProductionCountryFactory());
    }

    @Test
    public void populate_works() throws Exception
    {
        ContentValues values = new ContentValues();
        ProductionCountryDTO dto = new ProductionCountryDTO(CountryCode.GB, "United Kingdom");
        factory.populate(values, dto);
        assertThat(values.getAsString(ProductionCountryContract._ID)).isEqualTo("GB");
        assertThat(values.getAsString(ProductionCountryContract.COLUMN_NAME)).isEqualTo("United Kingdom");
    }

    @Test
    public void createSingle_works() throws Exception
    {
        ProductionCountryDTO dto = new ProductionCountryDTO(CountryCode.GB, "United Kingdom");
        ContentValues values = factory.createFrom(dto);
        assertThat(values.getAsString(ProductionCountryContract._ID)).isEqualTo("GB");
        assertThat(values.getAsString(ProductionCountryContract.COLUMN_NAME)).isEqualTo("United Kingdom");
    }

    @Test
    public void createVectorFromCollection_callsSingle() throws Exception
    {
        ProductionCountryDTO dto1 = new ProductionCountryDTO(CountryCode.GB, "United Kingdom");
        ProductionCountryDTO dto2 = new ProductionCountryDTO(CountryCode.US, "United States of America");
        Vector<ContentValues> values = factory.createFrom(Arrays.asList(dto1, dto2));
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0).getAsString(ProductionCountryContract._ID)).isEqualTo("GB");
        assertThat(values.get(0).getAsString(ProductionCountryContract.COLUMN_NAME)).isEqualTo("United Kingdom");
        assertThat(values.get(1).getAsString(ProductionCountryContract._ID)).isEqualTo("US");
        assertThat(values.get(1).getAsString(ProductionCountryContract.COLUMN_NAME)).isEqualTo("United States of America");
    }
}