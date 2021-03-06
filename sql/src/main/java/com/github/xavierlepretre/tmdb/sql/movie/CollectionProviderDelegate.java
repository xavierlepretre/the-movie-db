package com.github.xavierlepretre.tmdb.sql.movie;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.movie.CollectionContract;
import com.github.xavierlepretre.tmdb.model.movie.CollectionId;
import com.github.xavierlepretre.tmdb.sql.EntityProviderDelegate;
import com.github.xavierlepretre.tmdb.sql.notify.NotificationListInsert;
import com.github.xavierlepretre.tmdb.sql.notify.NotificationListWithCount;

import java.util.Collections;

public class CollectionProviderDelegate implements EntityProviderDelegate
{
    private static final int COLLECTIONS = 100;
    private static final int COLLECTION_BY_ID = 101;
    private static final int INDEX_INSERT_RESOLVE_CONFLICT_REPLACE = 5;

    @NonNull private final String contentAuthority;
    @NonNull private final Uri entityContentUri;
    @NonNull private final String contentDirType;
    @NonNull private final String contentItemType;
    @NonNull private final UriMatcher uriMatcher;

    public CollectionProviderDelegate(
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
        uriMatcher.addURI(contentAuthority, CollectionContract.PATH, COLLECTIONS);
        uriMatcher.addURI(contentAuthority, CollectionContract.PATH + "/#", COLLECTION_BY_ID);
    }

    @Override public void registerWith(@NonNull UriMatcher uriMatcher, int outsideMatch)
    {
        uriMatcher.addURI(contentAuthority, CollectionContract.PATH, outsideMatch);
        uriMatcher.addURI(contentAuthority, CollectionContract.PATH + "/#", outsideMatch);
    }

    @Override @Nullable public String getType(@NonNull Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case COLLECTIONS:
                return contentDirType;
            case COLLECTION_BY_ID:
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
            case COLLECTIONS:
                return getAllCollections(readableDb,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sortOrder,
                        limit);
            case COLLECTION_BY_ID:
                return getCollectionById(readableDb,
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

    @Nullable public Cursor getAllCollections(
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
        queryBuilder.setTables(CollectionContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @Nullable public Cursor getCollectionById(
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
        String id = getCollectionId(uri);
        String newSelection = CollectionContract.TABLE_NAME + "." + CollectionContract._ID + "=?";
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
        queryBuilder.setTables(CollectionContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @NonNull public String getCollectionId(@NonNull Uri uri)
    {
        return uri.getPathSegments().get(1);
    }

    @NonNull @Override public NotificationListInsert insert(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values)
    {
        switch (uriMatcher.match(uri))
        {
            case COLLECTIONS:
                if (values != null && values.getAsLong(CollectionContract._ID) == null)
                {
                    throw new SQLiteConstraintException(CollectionContract._ID + " cannot be null");
                }
                long id = writableDb.insertWithOnConflict(
                        CollectionContract.TABLE_NAME,
                        null,
                        values,
                        INDEX_INSERT_RESOLVE_CONFLICT_REPLACE);
                if (id <= 0)
                {
                    return new NotificationListInsert(Collections.<Uri>emptyList(), null);
                }
                Uri inserted = buildCollectionLocation(id);
                return new NotificationListInsert(Collections.singleton(inserted), inserted);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @NonNull @Override public NotificationListWithCount bulkInsert(
            @NonNull SQLiteDatabase writableDb, @NonNull Uri uri, @NonNull ContentValues[] values)
    {
        switch (uriMatcher.match(uri))
        {
            case COLLECTIONS:
                writableDb.beginTransaction();
                int returnCount = 0;
                long insertedId;
                try
                {
                    for (ContentValues value : values)
                    {
                        if (value == null || value.getAsLong(CollectionContract._ID) == null)
                        {
                            continue;
                        }
                        insertedId = writableDb.insertWithOnConflict(
                                CollectionContract.TABLE_NAME,
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
                return new NotificationListWithCount(Collections.singleton(uri), returnCount);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @NonNull public Uri buildCollectionLocation(@NonNull CollectionId id)
    {
        return buildCollectionLocation(id.getId());
    }

    @NonNull private Uri buildCollectionLocation(long id)
    {
        return buildCollectionLocation(entityContentUri, id);
    }

    @NonNull public static Uri buildCollectionLocation(
            @NonNull Uri entityContentUri,
            @NonNull CollectionId id)
    {
        return buildCollectionLocation(entityContentUri, id.getId());
    }

    @NonNull private static Uri buildCollectionLocation(
            @NonNull Uri entityContentUri,
            long id)
    {
        return entityContentUri.buildUpon()
                .appendPath(Long.toString(id))
                .build();
    }

    @NonNull @Override public NotificationListWithCount delete(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        switch (uriMatcher.match(uri))
        {
            case COLLECTIONS:
                return deleteCollections(writableDb,
                        uri,
                        selection,
                        selectionArgs);
            case COLLECTION_BY_ID:
                return deleteCollectionById(writableDb,
                        uri,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @NonNull public NotificationListWithCount deleteCollections(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return new NotificationListWithCount(
                Collections.singleton(uri),
                writableDb.delete(CollectionContract.TABLE_NAME, selection, selectionArgs));
    }

    @NonNull public NotificationListWithCount deleteCollectionById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getCollectionId(uri);
        String newSelection = CollectionContract.TABLE_NAME + "." + CollectionContract._ID + "=?";
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
        return new NotificationListWithCount(
                Collections.singleton(uri),
                writableDb.delete(
                        CollectionContract.TABLE_NAME,
                        selection == null ? newSelection : selection + " AND " + newSelection,
                        newSelectionArgs));
    }

    @NonNull @Override public NotificationListWithCount update(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        switch (uriMatcher.match(uri))
        {
            case COLLECTIONS:
                return updateCollections(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);
            case COLLECTION_BY_ID:
                return updateCollectionById(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @NonNull public NotificationListWithCount updateCollections(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return new NotificationListWithCount(
                Collections.singleton(uri),
                writableDb.update(CollectionContract.TABLE_NAME,
                        values, selection, selectionArgs));
    }

    @NonNull public NotificationListWithCount updateCollectionById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getCollectionId(uri);
        String newSelection = CollectionContract.TABLE_NAME + "." + CollectionContract._ID + "=?";
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
        return new NotificationListWithCount(
                Collections.singleton(uri),
                writableDb.update(
                        CollectionContract.TABLE_NAME,
                        values,
                        selection == null ? newSelection : selection + " AND " + newSelection,
                        newSelectionArgs));
    }
}
