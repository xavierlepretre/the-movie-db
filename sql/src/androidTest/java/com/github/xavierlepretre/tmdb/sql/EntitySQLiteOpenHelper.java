package com.github.xavierlepretre.tmdb.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public class EntitySQLiteOpenHelper extends SQLiteOpenHelper
{
    @NonNull private final EntitySQLHelperDelegate[] sQLHelperDelegates;

    public EntitySQLiteOpenHelper(
            Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
            @NonNull EntitySQLHelperDelegate... sQLHelperDelegates)
    {
        super(context, name, factory, version);
        this.sQLHelperDelegates = sQLHelperDelegates;
    }

    @Override public void onCreate(SQLiteDatabase db)
    {
        for (EntitySQLHelperDelegate helper : sQLHelperDelegates)
        {
            db.execSQL(helper.getCreateQuery());
        }
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        for (EntitySQLHelperDelegate helper : sQLHelperDelegates)
        {
            db.execSQL(helper.getUpgradeQuery(oldVersion, newVersion));
        }
    }
}
