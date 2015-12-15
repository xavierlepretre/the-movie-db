package com.github.xavierlepretre.tmdb.model.movie;

import com.github.xavierlepretre.tmdb.model.EntityProviderDelegate;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public class MovieProviderDelegate implements EntityProviderDelegate
{
    private static final int MOVIES = 100;
    private static final int MOVIE_BY_ID = 101;
    private static final int MOVIES_IN_COLLECTION = 102;
    private static final int INDEX_INSERT_RESOLVE_CONFLICT_REPLACE = 5;

    @NonNull private final String contentAuthority;
    @NonNull private final Uri entityContentUri;
    @NonNull private final Uri collectionEntityContentUri;
    @NonNull private final String contentDirType;
    @NonNull private final String contentItemType;
    @NonNull private final UriMatcher uriMatcher;

    public MovieProviderDelegate(
            @NonNull String contentAuthority,
            @NonNull Uri entityContentUri,
            @NonNull Uri collectionEntityContentUri,
            @NonNull String contentDirType,
            @NonNull String contentItemType)
    {
        this.contentAuthority = contentAuthority;
        this.entityContentUri = entityContentUri;
        this.collectionEntityContentUri = collectionEntityContentUri;
        this.contentDirType = contentDirType;
        this.contentItemType = contentItemType;
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH, MOVIES);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH + "/#", MOVIE_BY_ID);
        uriMatcher.addURI(contentAuthority, CollectionContract.PATH + "/#/" + MovieContract.PATH, MOVIES_IN_COLLECTION);
    }

    @Override public void registerWith(@NonNull UriMatcher uriMatcher, int outsideMatch)
    {
        uriMatcher.addURI(contentAuthority, MovieContract.PATH, outsideMatch);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH + "/#", outsideMatch);
        uriMatcher.addURI(contentAuthority, CollectionContract.PATH + "/#/" + MovieContract.PATH, outsideMatch);
    }

    @Override @Nullable public String getType(@NonNull Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case MOVIES:
            case MOVIES_IN_COLLECTION:
                return contentDirType;
            case MOVIE_BY_ID:
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
            case MOVIES:
                return getAllMovies(readableDb,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sortOrder,
                        limit);
            case MOVIE_BY_ID:
                return getMovieById(readableDb,
                        uri,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sortOrder,
                        limit);
            case MOVIES_IN_COLLECTION:
                return getMoviesInCollection(readableDb,
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

    @Nullable public Cursor getAllMovies(
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
        queryBuilder.setTables(MovieContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @Nullable public Cursor getMovieById(
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
        String id = getMovieId(uri);
        String newSelection = MovieContract.TABLE_NAME + "." + MovieContract._ID + "=?";
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
        queryBuilder.setTables(MovieContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @Nullable public Cursor getMoviesInCollection(
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
        String newSelection = MovieContract.TABLE_NAME + "." + MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID + "=?";
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
        queryBuilder.setTables(MovieContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @NonNull public String getMovieId(@NonNull Uri uri)
    {
        List<String> pathSegments = uri.getPathSegments();
        if (!pathSegments.get(0).equals(MovieContract.PATH))
        {
            throw new IllegalArgumentException(uri.toString() + " does not describe a movie");
        }
        return pathSegments.get(1);
    }

    @NonNull public String getCollectionId(@NonNull Uri uri)
    {
        List<String> pathSegments = uri.getPathSegments();
        if (!pathSegments.get(0).equals(CollectionContract.PATH))
        {
            throw new IllegalArgumentException(uri.toString() + " does not describe a collection");
        }
        return pathSegments.get(1);
    }

    @Override @Nullable public Uri insert(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values)
    {
        switch (uriMatcher.match(uri))
        {
            case MOVIES:
                if (values != null && values.getAsLong(MovieContract._ID) == null)
                {
                    throw new SQLiteConstraintException(MovieContract._ID + " cannot be null");
                }
                long id = writableDb.insertWithOnConflict(
                        MovieContract.TABLE_NAME,
                        null,
                        values,
                        INDEX_INSERT_RESOLVE_CONFLICT_REPLACE);
                if (id <= 0)
                {
                    return null;
                }
                return buildMovieLocation(id);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override public int bulkInsert(
            @NonNull SQLiteDatabase writableDb, @NonNull Uri uri, @NonNull ContentValues[] values)
    {
        switch (uriMatcher.match(uri))
        {
            case MOVIES:
                writableDb.beginTransaction();
                int returnCount = 0;
                long insertedId;
                try
                {
                    for (ContentValues value : values)
                    {
                        if (value == null || value.getAsLong(MovieContract._ID) == null)
                        {
                            continue;
                        }
                        insertedId = writableDb.insertWithOnConflict(
                                MovieContract.TABLE_NAME,
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

    @NonNull public Uri buildMovieLocation(@NonNull MovieId id)
    {
        return buildMovieLocation(entityContentUri, id.getId());
    }

    @NonNull private Uri buildMovieLocation(long id)
    {
        return buildMovieLocation(entityContentUri, id);
    }

    @NonNull public static Uri buildMovieLocation(@NonNull Uri entityContentUri, @NonNull MovieId id)
    {
        return buildMovieLocation(entityContentUri, id.getId());
    }

    @NonNull private static Uri buildMovieLocation(@NonNull Uri entityContentUri, long id)
    {
        return entityContentUri.buildUpon()
                .appendPath(Long.toString(id))
                .build();
    }

    @NonNull public Uri buildCollectionMoviesLocation(@NonNull CollectionId id)
    {
        return buildCollectionMoviesLocation(collectionEntityContentUri, id.getId());
    }

    @NonNull private Uri buildCollectionMoviesLocation(long id)
    {
        return buildCollectionMoviesLocation(collectionEntityContentUri, id);
    }

    @NonNull public static Uri buildCollectionMoviesLocation(@NonNull Uri collectionEntityContentUri, @NonNull CollectionId id)
    {
        return buildCollectionMoviesLocation(collectionEntityContentUri, id.getId());
    }

    @NonNull private static Uri buildCollectionMoviesLocation(@NonNull Uri collectionEntityContentUri, long id)
    {
        return collectionEntityContentUri.buildUpon()
                .appendPath(Long.toString(id))
                .appendPath(MovieContract.PATH)
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
            case MOVIES:
                return deleteMovies(writableDb,
                        selection,
                        selectionArgs);
            case MOVIE_BY_ID:
                return deleteMovieById(writableDb,
                        uri,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public int deleteMovies(
            @NonNull SQLiteDatabase writableDb,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return writableDb.delete(MovieContract.TABLE_NAME, selection, selectionArgs);
    }

    public int deleteMovieById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getMovieId(uri);
        String newSelection = MovieContract.TABLE_NAME + "." + MovieContract._ID + "=?";
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
                MovieContract.TABLE_NAME,
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
            case MOVIES:
                return updateMovies(writableDb,
                        values,
                        selection,
                        selectionArgs);
            case MOVIE_BY_ID:
                return updateMovieById(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public int updateMovies(
            @NonNull SQLiteDatabase writableDb,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return writableDb.update(MovieContract.TABLE_NAME,
                values, selection, selectionArgs);
    }

    public int updateMovieById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getMovieId(uri);
        String newSelection = MovieContract.TABLE_NAME + "." + MovieContract._ID + "=?";
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
                MovieContract.TABLE_NAME,
                values,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs);
    }
}
