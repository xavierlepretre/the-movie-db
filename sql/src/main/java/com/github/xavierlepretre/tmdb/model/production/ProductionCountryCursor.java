package com.github.xavierlepretre.tmdb.model.production;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.annotation.NonNull;

import com.neovisionaries.i18n.CountryCode;

public class ProductionCountryCursor extends CursorWrapper
{
    private final int iso3166Dash1Col;
    private final int nameCol;

    public ProductionCountryCursor(@NonNull Cursor cursor)
    {
        super(cursor);
        iso3166Dash1Col = getColumnIndex(ProductionCountryContract._ID);
        nameCol = getColumnIndex(ProductionCountryContract.COLUMN_NAME);
    }

    @NonNull public ProductionCountry getProductionCountry()
    {
        return new ProductionCountry(
                (iso3166Dash1Col < 0 || isNull(iso3166Dash1Col)) ? null : CountryCode.getByCode(getString(iso3166Dash1Col)),
                nameCol < 0 ? null : getString(nameCol));
    }
}
