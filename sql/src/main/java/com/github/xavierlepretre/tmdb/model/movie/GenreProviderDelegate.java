package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.EntityProviderDelegate;

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

    @Override @NonNull public String getCreateQuery()
    {
        return "CREATE TABLE " + GenreContract.TABLE_NAME + "("
                + GenreContract._ID + " INTEGER PRIMARY KEY,"
                + GenreContract.COLUMN_NAME + " TEXT NULL"
                + ");";
    }

    @NonNull @Override public String getUpgradeQuery(int oldVersion, int newVersion)
    {
        return "DROP TABLE IF EXISTS " + GenreContract.TABLE_NAME + ";";
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

    @Override @Nullable public Uri insert(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values)
    {
        switch (uriMatcher.match(uri))
        {
            case GENRES:
                long id = writableDb.insertWithOnConflict(
                        GenreContract.TABLE_NAME,
                        null,
                        values,
                        INDEX_INSERT_RESOLVE_CONFLICT_REPLACE);
                if (id <= 0)
                {
                    return null;
                }
                return buildGenreLocation(id);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override public int bulkInsert(
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
                        insertedId = writableDb.insert(GenreContract.TABLE_NAME, null, value);
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

    @NonNull public Uri buildGenreLocation(long id)
    {
        return buildGenreLocation(entityContentUri, id);
    }

    @NonNull public static Uri buildGenreLocation(@NonNull Uri entityContentUri, long id)
    {
        return entityContentUri.buildUpon()
                .appendPath(Long.toString(id))
                .build();
    }

    @Override public int delete(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        switch (uriMatcher.match(uri))
        {
            case GENRES:
                return deleteGenres(writableDb,
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

    public int deleteGenres(
            @NonNull SQLiteDatabase writableDb,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return writableDb.delete(GenreContract.TABLE_NAME, selection, selectionArgs);
    }

    public int deleteGenreById(
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
        return writableDb.delete(
                GenreContract.TABLE_NAME,
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
            case GENRES:
                return updateGenres(writableDb,
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

    public int updateGenres(
            @NonNull SQLiteDatabase writableDb,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return writableDb.update(GenreContract.TABLE_NAME,
                values, selection, selectionArgs);
    }

    public int updateGenreById(
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
        return writableDb.update(
                GenreContract.TABLE_NAME,
                values,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs);
    }
}
