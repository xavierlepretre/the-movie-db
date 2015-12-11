package com.github.xavierlepretre.tmdb.model.conf;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigurationSQLiteOpenHelper extends SQLiteOpenHelper
{
    ConfigurationProviderDelegate configurationProviderDelegate;

    public ConfigurationSQLiteOpenHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        this.configurationProviderDelegate = mock(ConfigurationProviderDelegate.class);
        when(configurationProviderDelegate.getCreateQuery()).thenCallRealMethod();
        when(configurationProviderDelegate.getUpgradeQuery(anyInt(), anyInt())).thenCallRealMethod();
    }

    @Override public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(configurationProviderDelegate.getCreateQuery());
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(configurationProviderDelegate.getUpgradeQuery(oldVersion, newVersion));
    }
}
