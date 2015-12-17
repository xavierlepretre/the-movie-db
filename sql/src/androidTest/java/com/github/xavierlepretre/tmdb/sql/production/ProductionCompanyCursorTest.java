package com.github.xavierlepretre.tmdb.sql.production;

import com.github.xavierlepretre.tmdb.model.production.ProductionCompany;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyContract;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyId;

import android.database.MatrixCursor;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ProductionCompanyCursorTest
{
    private static final String[] COLUMNS = new String[]{
            ProductionCompanyContract._ID,
            ProductionCompanyContract.COLUMN_NAME
    };
    private static final String[] VALUES = new String[]{
            "5",
            "Columbia Pictures"
    };

    @Test
    public void mayCreateProductionCompany() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(VALUES);
        ProductionCompanyCursor entityCursor = new ProductionCompanyCursor(cursor);
        entityCursor.moveToFirst();

        ProductionCompany productionCompany = entityCursor.getProductionCompany();
        assertThat(productionCompany.getId()).isEqualTo(new ProductionCompanyId(5));
        assertThat(productionCompany.getName()).isEqualTo("Columbia Pictures");
    }

    @Test
    public void mayCreateProductionCompanyWithMissing() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(new String[0]);
        cursor.addRow(new String[0]);
        ProductionCompanyCursor entityCursor = new ProductionCompanyCursor(cursor);
        entityCursor.moveToFirst();

        ProductionCompany productionCompany = entityCursor.getProductionCompany();
        assertThat(productionCompany.getId()).isNull();
        assertThat(productionCompany.getName()).isNull();
    }

    @Test
    public void mayCreateProductionCompanyWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(new String[COLUMNS.length]);
        ProductionCompanyCursor entityCursor = new ProductionCompanyCursor(cursor);
        entityCursor.moveToFirst();

        ProductionCompany productionCompany = entityCursor.getProductionCompany();
        assertThat(productionCompany.getId()).isNull();
        assertThat(productionCompany.getName()).isNull();
    }
}