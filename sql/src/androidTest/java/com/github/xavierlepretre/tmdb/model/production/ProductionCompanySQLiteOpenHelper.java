package com.github.xavierlepretre.tmdb.model.production;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductionCompanySQLiteOpenHelper extends SQLiteOpenHelper
{
    ProductionCompanyProviderDelegate productionCompanyProviderDelegate;

    public ProductionCompanySQLiteOpenHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        this.productionCompanyProviderDelegate = mock(ProductionCompanyProviderDelegate.class);
        when(productionCompanyProviderDelegate.getCreateQuery()).thenCallRealMethod();
        when(productionCompanyProviderDelegate.getUpgradeQuery(anyInt(), anyInt())).thenCallRealMethod();
    }

    @Override public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(productionCompanyProviderDelegate.getCreateQuery());
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(productionCompanyProviderDelegate.getUpgradeQuery(oldVersion, newVersion));
    }
}
