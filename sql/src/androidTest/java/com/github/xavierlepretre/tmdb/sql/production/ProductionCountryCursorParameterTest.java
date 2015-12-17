package com.github.xavierlepretre.tmdb.sql.production;

import android.database.MatrixCursor;

import com.github.xavierlepretre.tmdb.sql.ParameterColumnValue;
import com.github.xavierlepretre.tmdb.model.production.ProductionCountry;
import com.github.xavierlepretre.tmdb.model.production.ProductionCountryContract;
import com.neovisionaries.i18n.CountryCode;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ProductionCountryCursorParameterTest
{
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{ProductionCountryContract._ID, "GB"},
            new String[]{ProductionCountryContract.COLUMN_NAME, "United Kingdom"}
    };

    @Parameterized.Parameter
    public ParameterColumnValue parameter;

    @Parameterized.Parameters()
    public static ParameterColumnValue[] getParameters()
    {
        return ParameterColumnValue.getPossibleParameters(POTENTIAL_COLUMNS);
    }

    @Test
    public void mayCreateProductionCountryWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(parameter.columns.toArray(new String[parameter.columns.size()]));
        for (List<String> row : parameter.rows)
        {
            cursor.addRow(row);
        }
        ProductionCountryCursor entityCursor = new ProductionCountryCursor(cursor);
        entityCursor.moveToFirst();

        ProductionCountry productionCountry = entityCursor.getProductionCountry();
        assertThat(productionCountry.getIso3166Dash1()).isEqualTo(
                parameter.columns.contains(ProductionCountryContract._ID)
                        ? CountryCode.GB
                        : null);
        assertThat(productionCountry.getName()).isEqualTo(
                parameter.columns.contains(ProductionCountryContract.COLUMN_NAME)
                        ? "United Kingdom"
                        : null);
    }
}