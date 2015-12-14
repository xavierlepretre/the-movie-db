package com.github.xavierlepretre.tmdb.model.production;

import android.content.ContentValues;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;
import com.github.xavierlepretre.tmdb.model.ParameterColumnValue;
import com.neovisionaries.i18n.CountryCode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(Parameterized.class)
public class ProductionCountryCursorWithProviderTest
{
    private static final String TEMP_DB_NAME = "temp.productionCountry.db";
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{ProductionCountryContract._ID, "GB", "US"},
            new String[]{ProductionCountryContract.COLUMN_NAME, "United Kingdom", "United States of America"}
    };

    private ProductionCountryProviderDelegate providerDelegate;
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
        providerDelegate = spy(new ProductionCountryProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/productionCountry"),
                "dir_type",
                "item_type"));
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new ProductionCountrySQLHelperDelegate());

        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        for (String[] pair : POTENTIAL_COLUMNS)
        {
            values1.put(pair[0], pair[1]);
            values2.put(pair[0], pair[2]);
        }
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
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
        ProductionCountryCursor found = new ProductionCountryCursor(providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                parameter.columns.toArray(new String[parameter.columns.size()]),
                null, null, null, null,
                ProductionCountryContract._ID + " ASC", null));

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        ProductionCountry productionCountry = found.getProductionCountry();
        assertThat(productionCountry.getIso3166Dash1()).isEqualTo(
                (parameter.columns.contains(ProductionCountryContract._ID) || parameter.columns.size() == 0)
                        ? CountryCode.GB
                        : null);
        assertThat(productionCountry.getName()).isEqualTo(
                (parameter.columns.contains(ProductionCountryContract.COLUMN_NAME) || parameter.columns.size() == 0)
                        ? "United Kingdom"
                        : null);
        assertThat(found.moveToNext()).isTrue();
        productionCountry = found.getProductionCountry();
        assertThat(productionCountry.getIso3166Dash1()).isEqualTo(
                (parameter.columns.contains(ProductionCountryContract._ID) || parameter.columns.size() == 0)
                        ? CountryCode.US
                        : null);
        assertThat(productionCountry.getName()).isEqualTo(
                (parameter.columns.contains(ProductionCountryContract.COLUMN_NAME) || parameter.columns.size() == 0)
                        ? "United States of America"
                        : null);
        assertThat(found.moveToNext()).isFalse();
    }
}
