package com.github.xavierlepretre.tmdb.model.conf;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.EntityProviderDelegate;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract.ImagesConfSegment;

public class ConfigurationProviderDelegate implements EntityProviderDelegate
{
    private static final int CONFIGURATION = 100;
    private static final int INDEX_INSERT_RESOLVE_CONFLICT_REPLACE = 5;

    @NonNull private final String contentAuthority;
    @NonNull private final Uri entityContentUri;
    @NonNull private final String contentItemType;
    @NonNull private final UriMatcher uriMatcher;

    public ConfigurationProviderDelegate(
            @NonNull String contentAuthority,
            @NonNull Uri entityContentUri,
            @NonNull String contentItemType)
    {
        this.contentAuthority = contentAuthority;
        this.entityContentUri = entityContentUri;
        this.contentItemType = contentItemType;
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(contentAuthority, ConfigurationContract.PATH, CONFIGURATION);
    }

    @Override @NonNull public String getCreateQuery()
    {
        return "CREATE TABLE " + ConfigurationContract.TABLE_NAME + "("
                + ConfigurationContract._ID + " INTEGER PRIMARY KEY,"
                + ImagesConfSegment.COLUMN_BASE_URL + " TEXT NULL,"
                + ImagesConfSegment.COLUMN_SECURE_BASE_URL + " TEXT NULL,"
                + ImagesConfSegment.COLUMN_BACKDROP_SIZES + " TEXT NULL,"
                + ImagesConfSegment.COLUMN_LOGO_SIZES + " TEXT NULL,"
                + ImagesConfSegment.COLUMN_POSTER_SIZES + " TEXT NULL,"
                + ImagesConfSegment.COLUMN_PROFILE_SIZES + " TEXT NULL,"
                + ImagesConfSegment.COLUMN_STILL_SIZES + " TEXT NULL,"
                + ConfigurationContract.COLUMN_CHANGE_KEYS + " TEXT NULL"
                + ");";
    }

    @NonNull @Override public String getUpgradeQuery(int oldVersion, int newVersion)
    {
        return "DROP TABLE IF EXISTS " + ConfigurationContract.TABLE_NAME + ";";
    }

    @Override public void registerWith(@NonNull UriMatcher uriMatcher, int outsideMatch)
    {
        uriMatcher.addURI(contentAuthority, ConfigurationContract.PATH, outsideMatch);
    }

    @Override @Nullable public String getType(@NonNull Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case CONFIGURATION:
                return contentItemType;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override @Nullable public Cursor query(
            @NonNull SQLiteDatabase readableDb,
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String groupBy,
            @Nullable String having,
            @Nullable String sortOrder,
            @Nullable String limit)
    {
        switch (uriMatcher.match(uri))
        {
            case CONFIGURATION:
                return getConfiguration(readableDb,
                        uri,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sortOrder,
                        limit);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable public Cursor getAllConfigurations(
            @NonNull SQLiteDatabase readableDb,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String groupBy,
            @Nullable String having,
            @Nullable String sortOrder,
            @Nullable String limit)
    {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ConfigurationContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @Nullable public Cursor getConfiguration(
            @NonNull SQLiteDatabase readableDb,
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String groupBy,
            @Nullable String having,
            @Nullable String sortOrder,
            @Nullable String limit)
    {
        String id = String.format("%d", ConfigurationContract.UNIQUE_ROW_ID);
        String newSelection = ConfigurationContract.TABLE_NAME + "." + ConfigurationContract._ID + "=?";
        String[] newSelectionArgs = new String[selectionArgs == null ? 1 : selectionArgs.length + 1];
        int i = 0;
        if (selectionArgs != null)
        {
            for (i = 0; i < selectionArgs.length; i++)
            {
                newSelectionArgs[i] = selectionArgs[i];
            }
        }
        newSelectionArgs[i] = id;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ConfigurationContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @Override @Nullable public Uri insert(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values)
    {
        switch (uriMatcher.match(uri))
        {
            case CONFIGURATION:
                if (values == null)
                {
                    values = new ContentValues();
                }
                values.put(ConfigurationContract._ID, ConfigurationContract.UNIQUE_ROW_ID);
                long id = writableDb.insertWithOnConflict(
                        ConfigurationContract.TABLE_NAME,
                        null,
                        values,
                        INDEX_INSERT_RESOLVE_CONFLICT_REPLACE);
                if (id <= 0)
                {
                    return null;
                }
                return entityContentUri;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override public int bulkInsert(
            @NonNull SQLiteDatabase writableDb, @NonNull Uri uri, @NonNull ContentValues[] values)
    {
        throw new UnsupportedOperationException("Call insert");
    }

    @Override public int delete(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        switch (uriMatcher.match(uri))
        {
            case CONFIGURATION:
                return deleteConfiguration(writableDb,
                        uri,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public int deleteConfiguration(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = String.format("%d", ConfigurationContract.UNIQUE_ROW_ID);
        String newSelection = ConfigurationContract.TABLE_NAME + "." + ConfigurationContract._ID + "=?";
        String[] newSelectionArgs = new String[selectionArgs == null ? 1 : selectionArgs.length + 1];
        int i = 0;
        if (selectionArgs != null)
        {
            for (i = 0; i < selectionArgs.length; i++)
            {
                newSelectionArgs[i] = selectionArgs[i];
            }
        }
        newSelectionArgs[i] = id;
        return writableDb.delete(
                ConfigurationContract.TABLE_NAME,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs);
    }

    @Override public int update(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        switch (uriMatcher.match(uri))
        {
            case CONFIGURATION:
                return updateConfiguration(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public int updateConfiguration(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = String.format("%d", ConfigurationContract.UNIQUE_ROW_ID);
        String newSelection = ConfigurationContract.TABLE_NAME + "." + ConfigurationContract._ID + "=?";
        String[] newSelectionArgs = new String[selectionArgs == null ? 1 : selectionArgs.length + 1];
        int i = 0;
        if (selectionArgs != null)
        {
            for (i = 0; i < selectionArgs.length; i++)
            {
                newSelectionArgs[i] = selectionArgs[i];
            }
        }
        newSelectionArgs[i] = id;
        return writableDb.update(
                ConfigurationContract.TABLE_NAME,
                values,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs);
    }
}
