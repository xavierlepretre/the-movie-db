package com.github.xavierlepretre.tmdb.model.i18n;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.EntityProviderDelegate;
import com.neovisionaries.i18n.LanguageCode;

public class SpokenLanguageProviderDelegate implements EntityProviderDelegate
{
    private static final int SPOKEN_LANGUAGES = 100;
    private static final int SPOKEN_LANGUAGE_BY_ID = 101;
    private static final int INDEX_INSERT_RESOLVE_CONFLICT_REPLACE = 5;

    @NonNull private final String contentAuthority;
    @NonNull private final Uri entityContentUri;
    @NonNull private final String contentDirType;
    @NonNull private final String contentItemType;
    @NonNull private final UriMatcher uriMatcher;

    public SpokenLanguageProviderDelegate(
            @NonNull String contentAuthority,
            @NonNull Uri entityContentUri,
            @NonNull String contentDirType,
            @NonNull String contentItemType)
    {
        this.contentAuthority = contentAuthority;
        this.entityContentUri = entityContentUri;
        this.contentDirType = contentDirType;
        this.contentItemType = contentItemType;
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(contentAuthority, SpokenLanguageContract.PATH, SPOKEN_LANGUAGES);
        uriMatcher.addURI(contentAuthority, SpokenLanguageContract.PATH + "/*", SPOKEN_LANGUAGE_BY_ID);
    }

    @Override public void registerWith(@NonNull UriMatcher uriMatcher, int outsideMatch)
    {
        uriMatcher.addURI(contentAuthority, SpokenLanguageContract.PATH, outsideMatch);
        uriMatcher.addURI(contentAuthority, SpokenLanguageContract.PATH + "/*", outsideMatch);
    }

    @Override @Nullable public String getType(@NonNull Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case SPOKEN_LANGUAGES:
                return contentDirType;
            case SPOKEN_LANGUAGE_BY_ID:
                String id = getSpokenLanguageId(uri);
                if (LanguageCode.getByCode(id) != null)
                {
                    return contentItemType;
                }
                // Fall through.
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
            case SPOKEN_LANGUAGES:
                return getAllSpokenLanguages(readableDb,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sortOrder,
                        limit);
            case SPOKEN_LANGUAGE_BY_ID:
                return getSpokenLanguageById(readableDb,
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

    @Nullable public Cursor getAllSpokenLanguages(
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
        queryBuilder.setTables(SpokenLanguageContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @Nullable public Cursor getSpokenLanguageById(
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
        String id = getSpokenLanguageId(uri);
        String newSelection = SpokenLanguageContract.TABLE_NAME + "." + SpokenLanguageContract._ID + "=?";
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
        queryBuilder.setTables(SpokenLanguageContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @NonNull public String getSpokenLanguageId(@NonNull Uri uri)
    {
        return uri.getPathSegments().get(1);
    }

    @Override @Nullable public Uri insert(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values)
    {
        switch (uriMatcher.match(uri))
        {
            case SPOKEN_LANGUAGES:
                long id = writableDb.insertWithOnConflict(
                        SpokenLanguageContract.TABLE_NAME,
                        null,
                        values,
                        INDEX_INSERT_RESOLVE_CONFLICT_REPLACE);
                if (id <= 0)
                {
                    return null;
                }
                //noinspection ConstantConditions as it should have failed earlier
                return buildSpokenLanguageLocation(values.getAsString(SpokenLanguageContract._ID));
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override public int bulkInsert(
            @NonNull SQLiteDatabase writableDb, @NonNull Uri uri, @NonNull ContentValues[] values)
    {
        switch (uriMatcher.match(uri))
        {
            case SPOKEN_LANGUAGES:
                writableDb.beginTransaction();
                int returnCount = 0;
                long insertedId;
                try
                {
                    for (ContentValues value : values)
                    {
                        if (value == null || value.getAsString(SpokenLanguageContract._ID) == null)
                        {
                            continue;
                        }
                        insertedId = writableDb.insertWithOnConflict(
                                SpokenLanguageContract.TABLE_NAME,
                                null,
                                value,
                                INDEX_INSERT_RESOLVE_CONFLICT_REPLACE);
                        if (insertedId > 0)
                        {
                            returnCount++;
                        }
                    }
                    writableDb.setTransactionSuccessful();
                }
                finally
                {
                    writableDb.endTransaction();
                }
                return returnCount;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @NonNull public Uri buildSpokenLanguageLocation(@NonNull LanguageCode id)
    {
        return buildSpokenLanguageLocation(entityContentUri, id.name());
    }

    @NonNull private Uri buildSpokenLanguageLocation(@NonNull String id)
    {
        return buildSpokenLanguageLocation(entityContentUri, id);
    }

    @NonNull public static Uri buildSpokenLanguageLocation(
            @NonNull Uri entityContentUri,
            @NonNull LanguageCode id)
    {
        return buildSpokenLanguageLocation(entityContentUri, id.name());
    }

    @NonNull private static Uri buildSpokenLanguageLocation(
            @NonNull Uri entityContentUri,
            @NonNull String id)
    {
        return entityContentUri.buildUpon().appendPath(id).build();
    }

    @Override public int delete(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        switch (uriMatcher.match(uri))
        {
            case SPOKEN_LANGUAGES:
                return deleteSpokenLanguages(writableDb,
                        selection,
                        selectionArgs);
            case SPOKEN_LANGUAGE_BY_ID:
                return deleteSpokenLanguageById(writableDb,
                        uri,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public int deleteSpokenLanguages(
            @NonNull SQLiteDatabase writableDb,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return writableDb.delete(SpokenLanguageContract.TABLE_NAME, selection, selectionArgs);
    }

    public int deleteSpokenLanguageById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getSpokenLanguageId(uri);
        String newSelection = SpokenLanguageContract.TABLE_NAME + "." + SpokenLanguageContract._ID + "=?";
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
                SpokenLanguageContract.TABLE_NAME,
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
            case SPOKEN_LANGUAGES:
                return updateSpokenLanguages(writableDb,
                        values,
                        selection,
                        selectionArgs);
            case SPOKEN_LANGUAGE_BY_ID:
                return updateSpokenLanguageById(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public int updateSpokenLanguages(
            @NonNull SQLiteDatabase writableDb,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return writableDb.update(SpokenLanguageContract.TABLE_NAME,
                values, selection, selectionArgs);
    }

    public int updateSpokenLanguageById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getSpokenLanguageId(uri);
        String newSelection = SpokenLanguageContract.TABLE_NAME + "." + SpokenLanguageContract._ID + "=?";
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
                SpokenLanguageContract.TABLE_NAME,
                values,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs);
    }
}
