package com.github.xavierlepretre.tmdb.model.production;

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
                idCol < 0 ? null : new ProductionCompanyId(getInt(idCol)),
                nameCol < 0 ? null : getString(nameCol));
    }
}
