package com.github.xavierlepretre.tmdb.sql.production;

import android.database.MatrixCursor;

import com.github.xavierlepretre.tmdb.model.production.ProductionCountry;
import com.github.xavierlepretre.tmdb.model.production.ProductionCountryContract;
import com.neovisionaries.i18n.CountryCode;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ProductionCountryCursorTest
{
    private static final String[] COLUMNS = new String[]{
            ProductionCountryContract._ID,
            ProductionCountryContract.COLUMN_NAME
    };
    private static final String[] VALUES = new String[]{
            "GB",
            "United Kingdom"
    };

    @Test
    public void mayCreateProductionCountry() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(VALUES);
        ProductionCountryCursor entityCursor = new ProductionCountryCursor(cursor);
        entityCursor.moveToFirst();

        ProductionCountry productionCountry = entityCursor.getProductionCountry();
        assertThat(productionCountry.getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(productionCountry.getName()).isEqualTo("United Kingdom");
    }

    @Test
    public void mayCreateProductionCountryWithMissing() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(new String[0]);
        cursor.addRow(new String[0]);
        ProductionCountryCursor entityCursor = new ProductionCountryCursor(cursor);
        entityCursor.moveToFirst();

        ProductionCountry productionCountry = entityCursor.getProductionCountry();
        assertThat(productionCountry.getIso3166Dash1()).isNull();
        assertThat(productionCountry.getName()).isNull();
    }

    @Test
    public void mayCreateProductionCountryWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(new String[COLUMNS.length]);
        ProductionCountryCursor entityCursor = new ProductionCountryCursor(cursor);
        entityCursor.moveToFirst();

        ProductionCountry productionCountry = entityCursor.getProductionCountry();
        assertThat(productionCountry.getIso3166Dash1()).isNull();
        assertThat(productionCountry.getName()).isNull();
    }
}