package com.github.xavierlepretre.tmdb.model.production;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductionCountrySQLiteOpenHelper extends SQLiteOpenHelper
{
    ProductionCountryProviderDelegate productionCountryProviderDelegate;

    public ProductionCountrySQLiteOpenHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        this.productionCountryProviderDelegate = mock(ProductionCountryProviderDelegate.class);
        when(productionCountryProviderDelegate.getCreateQuery()).thenCallRealMethod();
        when(productionCountryProviderDelegate.getUpgradeQuery(anyInt(), anyInt())).thenCallRealMethod();
    }

    @Override public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(productionCountryProviderDelegate.getCreateQuery());
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(productionCountryProviderDelegate.getUpgradeQuery(oldVersion, newVersion));
    }
}
