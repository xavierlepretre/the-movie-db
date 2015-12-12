package com.github.xavierlepretre.tmdb.model.production;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Vector;

public class ContentValuesProductionCompanyFactory
{
    @NonNull public Vector<ContentValues> createFrom(@NonNull Collection<ProductionCompanyDTO> productionCompanyDTOs)
    {
        Vector<ContentValues> values = new Vector<>(productionCompanyDTOs.size());
        for (ProductionCompanyDTO productionCompanyDTO : productionCompanyDTOs)
        {
            values.add(createFrom(productionCompanyDTO));
        }
        return values;
    }

    @NonNull public ContentValues createFrom(@NonNull ProductionCompanyDTO productionCompanyDTO)
    {
        ContentValues contentValues = new ContentValues();
        populate(contentValues, productionCompanyDTO);
        return contentValues;
    }

    public void populate(@NonNull ContentValues contentValues, @NonNull ProductionCompanyDTO productionCompanyDTO)
    {
        contentValues.put(ProductionCompanyContract._ID, productionCompanyDTO.getId().getId());
        contentValues.put(ProductionCompanyContract.COLUMN_NAME, productionCompanyDTO.getName());
    }
}
