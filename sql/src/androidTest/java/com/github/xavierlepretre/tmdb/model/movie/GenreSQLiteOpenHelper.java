package com.github.xavierlepretre.tmdb.model.movie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GenreSQLiteOpenHelper extends SQLiteOpenHelper
{
    GenreProviderDelegate genreProviderDelegate;

    public GenreSQLiteOpenHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        this.genreProviderDelegate = mock(GenreProviderDelegate.class);
        when(genreProviderDelegate.getCreateQuery()).thenCallRealMethod();
        when(genreProviderDelegate.getUpgradeQuery(anyInt(), anyInt())).thenCallRealMethod();
    }

    @Override public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(genreProviderDelegate.getCreateQuery());
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(genreProviderDelegate.getUpgradeQuery(oldVersion, newVersion));
    }
}
