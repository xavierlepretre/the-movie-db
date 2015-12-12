package com.github.xavierlepretre.tmdb.model.movie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CollectionSQLiteOpenHelper extends SQLiteOpenHelper
{
    CollectionProviderDelegate collectionProviderDelegate;

    public CollectionSQLiteOpenHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        this.collectionProviderDelegate = mock(CollectionProviderDelegate.class);
        when(collectionProviderDelegate.getCreateQuery()).thenCallRealMethod();
        when(collectionProviderDelegate.getUpgradeQuery(anyInt(), anyInt())).thenCallRealMethod();
    }

    @Override public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(collectionProviderDelegate.getCreateQuery());
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(collectionProviderDelegate.getUpgradeQuery(oldVersion, newVersion));
    }
}
