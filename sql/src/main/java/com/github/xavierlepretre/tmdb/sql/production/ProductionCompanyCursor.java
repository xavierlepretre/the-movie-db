package com.github.xavierlepretre.tmdb.sql.production;

import com.github.xavierlepretre.tmdb.model.production.ProductionCompany;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyContract;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyId;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.annotation.NonNull;

public class ProductionCompanyCursor extends CursorWrapper
{
    private final int idCol;
    private final int nameCol;

    public ProductionCompanyCursor(@NonNull Cursor cursor)
    {
        super(cursor);
        idCol = getColumnIndex(ProductionCompanyContract._ID);
        nameCol = getColumnIndex(ProductionCompanyContract.COLUMN_NAME);
    }

    @NonNull public ProductionCompany getProductionCompany()
    {
        return new ProductionCompany(
                (idCol < 0 || isNull(idCol)) ? null : new ProductionCompanyId(getInt(idCol)),
                nameCol < 0 ? null : getString(nameCol));
    }
}
