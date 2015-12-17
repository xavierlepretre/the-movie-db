package com.github.xavierlepretre.tmdb.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

public class TmdbDbHelper extends SQLiteOpenHelper
{
    @NonNull private final SparseArray<EntitySQLHelperDelegate> helperDelegates;

    public TmdbDbHelper(
            @Nullable Context context,
            @NonNull String databaseName,
            @Nullable CursorFactory factory,
            int latestVersion,
            @NonNull SparseArray<EntitySQLHelperDelegate> helperDelegates)
    {
        super(context, databaseName, factory, latestVersion);
        this.helperDelegates = helperDelegates;
    }

    @Override public void onCreate(SQLiteDatabase db)
    {
        for (int i = 0; i < helperDelegates.size(); i++)
        {
            db.execSQL(helperDelegates.get(helperDelegates.keyAt(i))
                    .getCreateQuery());
        }
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        for (int i = 0; i < helperDelegates.size(); i++)
        {
            db.execSQL(helperDelegates.get(helperDelegates.keyAt(i))
                    .getUpgradeQuery(oldVersion, newVersion));
        }
        onCreate(db);
    }
}
