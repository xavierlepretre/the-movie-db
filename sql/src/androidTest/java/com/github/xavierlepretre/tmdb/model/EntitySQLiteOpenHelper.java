package com.github.xavierlepretre.tmdb.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public class EntitySQLiteOpenHelper extends SQLiteOpenHelper
{
    @NonNull private final EntitySQLHelperDelegate sQLHelperDelegate;

    public EntitySQLiteOpenHelper(
            Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
            @NonNull EntitySQLHelperDelegate sQLHelperDelegate)
    {
        super(context, name, factory, version);
        this.sQLHelperDelegate = sQLHelperDelegate;
    }

    @Override public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(sQLHelperDelegate.getCreateQuery());
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(sQLHelperDelegate.getUpgradeQuery(oldVersion, newVersion));
    }
}
