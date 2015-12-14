package com.github.xavierlepretre.tmdb.model.production;

import android.content.ContentValues;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;
import com.github.xavierlepretre.tmdb.model.ParameterColumnValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(Parameterized.class)
public class ProductionCompanyCursorWithProviderTest
{
    private static final String TEMP_DB_NAME = "temp.productionCompany.db";
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{ProductionCompanyContract._ID, "5", "6"},
            new String[]{ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures", "Fox"}
    };

    private ProductionCompanyProviderDelegate providerDelegate;
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
        providerDelegate = spy(new ProductionCompanyProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/productionCompany"),
                "dir_type",
                "item_type"));
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new ProductionCompanySQLHelperDelegate());

        ContentValues values1 = new ContentValues();
        ContentValues values2 = new ContentValues();
        for (String[] pair : POTENTIAL_COLUMNS)
        {
            values1.put(pair[0], pair[1]);
            values2.put(pair[0], pair[2]);
        }
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
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
        ProductionCompanyCursor found = new ProductionCompanyCursor(providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                parameter.columns.toArray(new String[parameter.columns.size()]),
                null, null, null, null,
                ProductionCompanyContract._ID + " ASC", null));

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        ProductionCompany productionCompany = found.getProductionCompany();
        assertThat(productionCompany.getId()).isEqualTo(
                (parameter.columns.contains(ProductionCompanyContract._ID) || parameter.columns.size() == 0)
                        ? new ProductionCompanyId(5)
                        : null);
        assertThat(productionCompany.getName()).isEqualTo(
                (parameter.columns.contains(ProductionCompanyContract.COLUMN_NAME) || parameter.columns.size() == 0)
                        ? "Columbia Pictures"
                        : null);
        assertThat(found.moveToNext()).isTrue();
        productionCompany = found.getProductionCompany();
        assertThat(productionCompany.getId()).isEqualTo(
                (parameter.columns.contains(ProductionCompanyContract._ID) || parameter.columns.size() == 0)
                        ? new ProductionCompanyId(6)
                        : null);
        assertThat(productionCompany.getName()).isEqualTo(
                (parameter.columns.contains(ProductionCompanyContract.COLUMN_NAME) || parameter.columns.size() == 0)
                        ? "Fox"
                        : null);
        assertThat(found.moveToNext()).isFalse();
    }
}
