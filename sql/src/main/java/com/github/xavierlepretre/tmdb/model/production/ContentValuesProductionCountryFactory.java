package com.github.xavierlepretre.tmdb.model.production;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Vector;

public class ContentValuesProductionCountryFactory
{
    @NonNull public Vector<ContentValues> createFrom(@NonNull Collection<ProductionCountryDTO> productionCountryDTOs)
    {
        Vector<ContentValues> values = new Vector<>(productionCountryDTOs.size());
        for (ProductionCountryDTO productionCountryDTO : productionCountryDTOs)
        {
            values.add(createFrom(productionCountryDTO));
        }
        return values;
    }

    @NonNull public ContentValues createFrom(@NonNull ProductionCountryDTO productionCountryDTO)
    {
        ContentValues contentValues = new ContentValues();
        populate(contentValues, productionCountryDTO);
        return contentValues;
    }

    public void populate(@NonNull ContentValues contentValues, @NonNull ProductionCountryDTO productionCountryDTO)
    {
        contentValues.put(ProductionCountryContract._ID, productionCountryDTO.getIso3166Dash1().getAlpha2());
        contentValues.put(ProductionCountryContract.COLUMN_NAME, productionCountryDTO.getName());
    }
}
