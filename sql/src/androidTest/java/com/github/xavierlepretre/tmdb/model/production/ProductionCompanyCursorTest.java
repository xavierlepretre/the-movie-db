package com.github.xavierlepretre.tmdb.model.production;

import android.database.MatrixCursor;

import com.github.xavierlepretre.tmdb.model.ParameterColumnValue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ProductionCompanyCursorTest
{
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{ProductionCompanyContract._ID, "5"},
            new String[]{ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures"}
    };

    @Parameterized.Parameter
    public ParameterColumnValue parameter;

    @Parameterized.Parameters()
    public static ParameterColumnValue[] getParameters()
    {
        return ParameterColumnValue.getPossibleParameters(POTENTIAL_COLUMNS);
    }

    @Test
    public void mayCreateProductionCompanyWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(parameter.columns.toArray(new String[parameter.columns.size()]));
        for (List<String> row : parameter.rows)
        {
            cursor.addRow(row);
        }
        ProductionCompanyCursor entityCursor = new ProductionCompanyCursor(cursor);
        entityCursor.moveToFirst();

        ProductionCompany productionCompany = entityCursor.getProductionCompany();
        assertThat(productionCompany.getId()).isEqualTo(
                parameter.columns.contains(ProductionCompanyContract._ID)
                        ? new ProductionCompanyId(5)
                        : null);
        assertThat(productionCompany.getName()).isEqualTo(
                parameter.columns.contains(ProductionCompanyContract.COLUMN_NAME)
                        ? "Columbia Pictures"
                        : null);
    }
}