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

import com.github.xavierlepretre.tmdb.model.movie.GenreContract;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;
import com.github.xavierlepretre.tmdb.sql.EntityProviderDelegate;
import com.github.xavierlepretre.tmdb.sql.notify.NotificationListInsert;
import com.github.xavierlepretre.tmdb.sql.notify.NotificationListWithCount;

import java.util.Collections;

public class GenreProviderDelegate implements EntityProviderDelegate
{
    private static final int GENRES = 100;
    private static final int GENRE_BY_ID = 101;
    private static final int INDEX_INSERT_RESOLVE_CONFLICT_REPLACE = 5;

    @NonNull private final String contentAuthority;
    @NonNull private final Uri entityContentUri;
    @NonNull private final String contentDirType;
    @NonNull private final String contentItemType;
    @NonNull private final UriMatcher uriMatcher;

    public GenreProviderDelegate(
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
        uriMatcher.addURI(contentAuthority, GenreContract.PATH, GENRES);
        uriMatcher.addURI(contentAuthority, GenreContract.PATH + "/#", GENRE_BY_ID);
    }

    @Override public void registerWith(@NonNull UriMatcher uriMatcher, int outsideMatch)
    {
        uriMatcher.addURI(contentAuthority, GenreContract.PATH, outsideMatch);
        uriMatcher.addURI(contentAuthority, GenreContract.PATH + "/#", outsideMatch);
    }

    @Override @Nullable public String getType(@NonNull Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case GENRES:
                return contentDirType;
            case GENRE_BY_ID:
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
            case GENRES:
                return getAllGenres(readableDb,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sortOrder,
                        limit);
            case GENRE_BY_ID:
                return getGenreById(readableDb,
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

    @Nullable public Cursor getAllGenres(
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
        queryBuilder.setTables(GenreContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @Nullable public Cursor getGenreById(
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
        String id = getGenreId(uri);
        String newSelection = GenreContract.TABLE_NAME + "." + GenreContract._ID + "=?";
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
        queryBuilder.setTables(GenreContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @NonNull public String getGenreId(@NonNull Uri uri)
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
            case GENRES:
                if (values != null && values.getAsLong(GenreContract._ID) == null)
                {
                    throw new SQLiteConstraintException(GenreContract._ID + " cannot be null");
                }
                long id = writableDb.insertWithOnConflict(
                        GenreContract.TABLE_NAME,
                        null,
                        values,
                        INDEX_INSERT_RESOLVE_CONFLICT_REPLACE);
                if (id <= 0)
                {
                    return new NotificationListInsert(Collections.<Uri>emptyList(), null);
                }
                Uri inserted = buildGenreLocation(id);
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
            case GENRES:
                writableDb.beginTransaction();
                int returnCount = 0;
                long insertedId;
                try
                {
                    for (ContentValues value : values)
                    {
                        if (value == null || value.getAsLong(GenreContract._ID) == null)
                        {
                            continue;
                        }
                        insertedId = writableDb.insertWithOnConflict(
                                GenreContract.TABLE_NAME,
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

    @NonNull public Uri buildGenreLocation(@NonNull GenreId id)
    {
        return buildGenreLocation(entityContentUri, id.getId());
    }

    @NonNull private Uri buildGenreLocation(long id)
    {
        return buildGenreLocation(entityContentUri, id);
    }

    @NonNull public static Uri buildGenreLocation(@NonNull Uri entityContentUri, @NonNull GenreId id)
    {
        return buildGenreLocation(entityContentUri, id.getId());
    }

    @NonNull private static Uri buildGenreLocation(@NonNull Uri entityContentUri, long id)
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
            case GENRES:
                return deleteGenres(writableDb,
                        uri,
                        selection,
                        selectionArgs);
            case GENRE_BY_ID:
                return deleteGenreById(writableDb,
                        uri,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @NonNull public NotificationListWithCount deleteGenres(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return new NotificationListWithCount(
                Collections.singleton(uri),
                writableDb.delete(GenreContract.TABLE_NAME, selection, selectionArgs));
    }

    @NonNull public NotificationListWithCount deleteGenreById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getGenreId(uri);
        String newSelection = GenreContract.TABLE_NAME + "." + GenreContract._ID + "=?";
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
                        GenreContract.TABLE_NAME,
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
            case GENRES:
                return updateGenres(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);
            case GENRE_BY_ID:
                return updateGenreById(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @NonNull public NotificationListWithCount updateGenres(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return new NotificationListWithCount(
                Collections.singleton(uri),
                writableDb.update(GenreContract.TABLE_NAME,
                        values, selection, selectionArgs));
    }

    @NonNull public NotificationListWithCount updateGenreById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getGenreId(uri);
        String newSelection = GenreContract.TABLE_NAME + "." + GenreContract._ID + "=?";
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
                        GenreContract.TABLE_NAME,
                        values,
                        selection == null ? newSelection : selection + " AND " + newSelection,
                        newSelectionArgs));
    }
}
