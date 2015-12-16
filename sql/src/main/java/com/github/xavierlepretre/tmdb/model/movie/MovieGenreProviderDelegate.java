package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.EntityProviderDelegate;
import com.github.xavierlepretre.tmdb.model.notify.NotificationListInsert;
import com.github.xavierlepretre.tmdb.model.notify.NotificationListWithCount;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieGenreProviderDelegate implements EntityProviderDelegate
{
    private static final int MOVIE_GENRES = 100;
    private static final int MOVIE_BY_ID_GENRES = 101;
    private static final int GENRE_BY_ID_MOVIES = 102;
    private static final int GENRE_BY_ID_MOVIE_BY_ID = 103;
    private static final int INDEX_INSERT_RESOLVE_CONFLICT_REPLACE = 5;

    @NonNull
    private final String contentAuthority;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    @NonNull
    private final Uri movieGenreEntityContentUri;
    @NonNull
    private final String movieGenreContentDirType;
    @NonNull
    private final String movieGenreContentItemType;
    @NonNull
    private final Uri genreEntityContentUri;
    @NonNull
    private final String genreContentDirType;
    @NonNull
    private final Uri movieEntityContentUri;
    @NonNull
    private final String movieContentDirType;
    @NonNull
    private final UriMatcher uriMatcher;

    public MovieGenreProviderDelegate(
            @NonNull String contentAuthority,
            @NonNull Uri movieGenreEntityContentUri,
            @NonNull String movieGenreContentDirType,
            @NonNull String movieGenreContentItemType,
            @NonNull Uri genreEntityContentUri,
            @NonNull String genreContentDirType,
            @NonNull Uri movieEntityContentUri,
            @NonNull String movieContentDirType)
    {
        this.contentAuthority = contentAuthority;
        this.movieGenreEntityContentUri = movieGenreEntityContentUri;
        this.movieGenreContentDirType = movieGenreContentDirType;
        this.movieGenreContentItemType = movieGenreContentItemType;
        this.genreEntityContentUri = genreEntityContentUri;
        this.genreContentDirType = genreContentDirType;
        this.movieEntityContentUri = movieEntityContentUri;
        this.movieContentDirType = movieContentDirType;
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(contentAuthority, MovieGenreContract.PATH, MOVIE_GENRES);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH + "/#/" + GenreContract.PATH, MOVIE_BY_ID_GENRES);
        uriMatcher.addURI(contentAuthority, GenreContract.PATH + "/#/" + MovieContract.PATH, GENRE_BY_ID_MOVIES);
        uriMatcher.addURI(contentAuthority, GenreContract.PATH + "/#/" + MovieContract.PATH + "/#", GENRE_BY_ID_MOVIE_BY_ID);
    }

    @Override public void registerWith(@NonNull UriMatcher uriMatcher, int outsideMatch)
    {
        uriMatcher.addURI(contentAuthority, MovieGenreContract.PATH, outsideMatch);
        uriMatcher.addURI(contentAuthority, MovieContract.PATH + "/#/" + GenreContract.PATH, outsideMatch);
        uriMatcher.addURI(contentAuthority, GenreContract.PATH + "/#/" + MovieContract.PATH, outsideMatch);
        uriMatcher.addURI(contentAuthority, GenreContract.PATH + "/#/" + MovieContract.PATH + "/#", outsideMatch);
    }

    @Override @Nullable public String getType(@NonNull Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case MOVIE_GENRES:
                return movieGenreContentDirType;
            case MOVIE_BY_ID_GENRES:
                return genreContentDirType;
            case GENRE_BY_ID_MOVIES:
                return movieContentDirType;
            case GENRE_BY_ID_MOVIE_BY_ID:
                return movieGenreContentItemType;
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
            case MOVIE_GENRES:
                return getAllMovieGenres(readableDb,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sortOrder,
                        limit);

            case MOVIE_BY_ID_GENRES:
                return getMovieByIdGenres(readableDb,
                        uri,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sortOrder,
                        limit);

            case GENRE_BY_ID_MOVIES:
                return getGenreByIdMovies(readableDb,
                        uri,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sortOrder,
                        limit);

            case GENRE_BY_ID_MOVIE_BY_ID:
                return getMovieGenreById(readableDb,
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

    @Nullable public Cursor getAllMovieGenres(
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
        queryBuilder.setTables(MovieGenreContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @Nullable public Cursor getMovieByIdGenres(
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
        String newSelection = MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_MOVIE_ID + "=?";
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
        queryBuilder.setTables(GenreContract.TABLE_NAME
                + " INNER JOIN " + MovieGenreContract.TABLE_NAME + " ON ("
                + MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_GENRE_ID
                + "=" + GenreContract.TABLE_NAME + "." + GenreContract._ID
                + ")");
        return queryBuilder.query(readableDb,
                projection,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @Nullable public Cursor getGenreByIdMovies(
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
        String newSelection = MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_GENRE_ID + "=?";
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
        queryBuilder.setTables(MovieContract.TABLE_NAME
                + " INNER JOIN " + MovieGenreContract.TABLE_NAME + " ON ("
                + MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_MOVIE_ID
                + "=" + MovieContract.TABLE_NAME + "." + MovieContract._ID
                + ")");
        return queryBuilder.query(readableDb,
                projection,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @Nullable public Cursor getMovieGenreById(
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
        String[] id = getMovieGenreId(uri);
        String newSelection = MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_GENRE_ID + "=? AND "
                + MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_MOVIE_ID + "=?";
        String[] newSelectionArgs = new String[selectionArgs == null ? 2 : selectionArgs.length + 2];
        int i = 0;
        if (selectionArgs != null)
        {
            for (i = 0; i < selectionArgs.length; i++)
            {
                newSelectionArgs[i] = selectionArgs[i];
            }
        }
        newSelectionArgs[i++] = id[0];
        newSelectionArgs[i] = id[1];
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MovieGenreContract.TABLE_NAME);
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

    @NonNull public String getGenreId(@NonNull Uri uri)
    {
        List<String> pathSegments = uri.getPathSegments();
        if (!pathSegments.get(0).equals(GenreContract.PATH))
        {
            throw new IllegalArgumentException(uri.toString() + " does not describe a genre");
        }
        return pathSegments.get(1);
    }

    @NonNull public String[] getMovieGenreId(@NonNull Uri uri)
    {
        List<String> pathSegments = uri.getPathSegments();
        if (!pathSegments.get(0).equals(GenreContract.PATH)
                || !pathSegments.get(2).equals(MovieContract.PATH))
        {
            throw new IllegalArgumentException(uri.toString() + " does not describe a movie genre id");
        }
        return new String[]{pathSegments.get(1), pathSegments.get(3)};
    }

    @NonNull @Override public NotificationListInsert insert(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values)
    {
        switch (uriMatcher.match(uri))
        {
            case MOVIE_GENRES:
                if (values != null)
                {
                    if (values.getAsLong(MovieGenreContract.COLUMN_GENRE_ID) == null)
                    {
                        throw new SQLiteConstraintException(MovieGenreContract.COLUMN_GENRE_ID + " cannot be null");
                    }
                    if (values.getAsLong(MovieGenreContract.COLUMN_MOVIE_ID) == null)
                    {
                        throw new SQLiteConstraintException(MovieGenreContract.COLUMN_MOVIE_ID + " cannot be null");
                    }
                }
                break;

            case MOVIE_BY_ID_GENRES:
                String movieId = getMovieId(uri);
                if (values != null)
                {
                    {
                        if (values.getAsLong(MovieGenreContract.COLUMN_GENRE_ID) == null)
                        {
                            throw new SQLiteConstraintException(MovieGenreContract.COLUMN_GENRE_ID + " cannot be null");
                        }
                    }
                    values.put(MovieGenreContract.COLUMN_MOVIE_ID, movieId);
                }
                break;

            case GENRE_BY_ID_MOVIES:
                String genreId = getGenreId(uri);
                if (values != null)
                {
                    {
                        if (values.getAsLong(MovieGenreContract.COLUMN_MOVIE_ID) == null)
                        {
                            throw new SQLiteConstraintException(MovieGenreContract.COLUMN_MOVIE_ID + " cannot be null");
                        }
                    }
                    values.put(MovieGenreContract.COLUMN_GENRE_ID, genreId);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        long id = writableDb.insertWithOnConflict(
                MovieGenreContract.TABLE_NAME,
                null,
                values,
                INDEX_INSERT_RESOLVE_CONFLICT_REPLACE);
        if (id <= 0)
        {
            return new NotificationListInsert(Collections.<Uri>emptyList(), null);
        }
        //noinspection ConstantConditions
        GenreId insertedGenreId = new GenreId(values.getAsInteger(MovieGenreContract.COLUMN_GENRE_ID));
        MovieId insertedMovieId = new MovieId(values.getAsLong(MovieGenreContract.COLUMN_MOVIE_ID));
        Uri inserted = buildMovieGenreLocation(
                insertedGenreId,
                insertedMovieId);
        return new NotificationListInsert(
                Arrays.asList(
                        movieGenreEntityContentUri,
                        inserted,
                        buildMovieGenreLocation(insertedGenreId),
                        buildMovieGenreLocation(insertedMovieId)),
                inserted);
    }

    @NonNull @Override public NotificationListWithCount bulkInsert(
            @NonNull SQLiteDatabase writableDb, @NonNull Uri uri, @NonNull ContentValues[] values)
    {
        int returnCount = 0;
        long insertedId;
        int match = uriMatcher.match(uri);
        String genreId = null;
        String movieId = null;
        Set<Uri> toNotify = new HashSet<>();
        toNotify.add(movieGenreEntityContentUri);
        toNotify.add(uri);
        switch (match)
        {
            case MOVIE_GENRES:
                break;

            case MOVIE_BY_ID_GENRES:
                movieId = getMovieId(uri);
                break;

            case GENRE_BY_ID_MOVIES:
                genreId = getGenreId(uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        writableDb.beginTransaction();
        try
        {
            writableDb.setTransactionSuccessful();
            for (ContentValues value : values)
            {
                if (value != null)
                {
                    switch (match)
                    {
                        case MOVIE_BY_ID_GENRES:
                            value.put(MovieGenreContract.COLUMN_MOVIE_ID, movieId);
                            break;

                        case GENRE_BY_ID_MOVIES:
                            value.put(MovieGenreContract.COLUMN_GENRE_ID, genreId);
                            break;
                    }
                    if (value.getAsInteger(MovieGenreContract.COLUMN_GENRE_ID) != null
                            && value.getAsLong(MovieGenreContract.COLUMN_MOVIE_ID) != null)
                    {
                        insertedId = writableDb.insertWithOnConflict(
                                MovieGenreContract.TABLE_NAME,
                                null,
                                value,
                                INDEX_INSERT_RESOLVE_CONFLICT_REPLACE);
                        if (insertedId > 0)
                        {
                            returnCount++;
                            toNotify.add(buildMovieGenreLocation(new GenreId(value.getAsInteger(MovieGenreContract.COLUMN_GENRE_ID))));
                            toNotify.add(buildMovieGenreLocation(new MovieId(value.getAsLong(MovieGenreContract.COLUMN_MOVIE_ID))));
                        }
                    }
                }
            }
        }
        finally
        {
            writableDb.endTransaction();
        }
        return new NotificationListWithCount(toNotify, returnCount);
    }

    @NonNull public Uri buildMovieGenreLocation(@NonNull GenreId genreId, @NonNull MovieId movieId)
    {
        return buildMovieGenreLocation(genreEntityContentUri, genreId, movieId);
    }

    @NonNull
    public static Uri buildMovieGenreLocation(@NonNull Uri genreEntityContentUri, @NonNull GenreId genreId, @NonNull MovieId movieId)
    {
        return genreEntityContentUri.buildUpon()
                .appendPath(Integer.toString(genreId.getId()))
                .appendPath(MovieContract.PATH)
                .appendPath(Long.toString(movieId.getId()))
                .build();
    }

    @NonNull public Uri buildMovieGenreLocation(@NonNull GenreId genreId)
    {
        return buildMovieGenreLocation(genreEntityContentUri, genreId);
    }

    @NonNull
    public static Uri buildMovieGenreLocation(@NonNull Uri genreEntityContentUri, @NonNull GenreId genreId)
    {
        return genreEntityContentUri.buildUpon()
                .appendPath(Integer.toString(genreId.getId()))
                .appendPath(MovieContract.PATH)
                .build();
    }

    @NonNull public Uri buildMovieGenreLocation(@NonNull MovieId movieId)
    {
        return buildMovieGenreLocation(movieEntityContentUri, movieId);
    }

    @NonNull
    public static Uri buildMovieGenreLocation(@NonNull Uri movieEntityContentUri, @NonNull MovieId movieId)
    {
        return movieEntityContentUri.buildUpon()
                .appendPath(Long.toString(movieId.getId()))
                .appendPath(GenreContract.PATH)
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
            case MOVIE_GENRES:
                return deleteMovieGenres(writableDb,
                        uri,
                        selection,
                        selectionArgs);

            case MOVIE_BY_ID_GENRES:
                return deleteMovieGenreByMovieId(writableDb,
                        uri,
                        selection,
                        selectionArgs);

            case GENRE_BY_ID_MOVIES:
                return deleteMovieGenreByGenreId(writableDb,
                        uri,
                        selection,
                        selectionArgs);

            case GENRE_BY_ID_MOVIE_BY_ID:
                return deleteMovieGenreById(writableDb,
                        uri,
                        selection,
                        selectionArgs);

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @NonNull public NotificationListWithCount deleteMovieGenres(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        Set<Uri> toNotify = new HashSet<>();
        toNotify.add(uri);
        toNotify.addAll(getNotifyUris(
                writableDb,
                selection,
                selectionArgs));
        return new NotificationListWithCount(
                toNotify,
                writableDb.delete(MovieGenreContract.TABLE_NAME, selection, selectionArgs));
    }

    @NonNull public NotificationListWithCount deleteMovieGenreByMovieId(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getMovieId(uri);
        String newSelection = MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_MOVIE_ID + "=?";
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
        Set<Uri> toNotify = new HashSet<>();
        toNotify.add(uri);
        toNotify.addAll(getNotifyUris(
                writableDb,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs));
        return new NotificationListWithCount(
                toNotify,
                writableDb.delete(
                        MovieGenreContract.TABLE_NAME,
                        selection == null ? newSelection : selection + " AND " + newSelection,
                        newSelectionArgs));
    }

    @NonNull public NotificationListWithCount deleteMovieGenreByGenreId(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getGenreId(uri);
        String newSelection = MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_GENRE_ID + "=?";
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
        Set<Uri> toNotify = new HashSet<>();
        toNotify.add(uri);
        toNotify.addAll(getNotifyUris(
                writableDb,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs));
        return new NotificationListWithCount(
                toNotify,
                writableDb.delete(
                        MovieGenreContract.TABLE_NAME,
                        selection == null ? newSelection : selection + " AND " + newSelection,
                        newSelectionArgs));
    }

    @NonNull public NotificationListWithCount deleteMovieGenreById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String[] ids = getMovieGenreId(uri);
        String newSelection = MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_GENRE_ID + "=?" +
                " AND " + MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_MOVIE_ID + "=?";
        String[] newSelectionArgs = new String[selectionArgs == null ? 2 : selectionArgs.length + 2];
        int i = 0;
        if (selectionArgs != null)
        {
            for (i = 0; i < selectionArgs.length; i++)
            {
                newSelectionArgs[i] = selectionArgs[i];
            }
        }
        newSelectionArgs[i++] = ids[0];
        newSelectionArgs[i] = ids[1];
        Set<Uri> toNotify = new HashSet<>();
        toNotify.add(uri);
        toNotify.add(buildMovieGenreLocation(new MovieId(Long.parseLong(ids[1]))));
        toNotify.addAll(getNotifyUris(
                writableDb,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs));
        return new NotificationListWithCount(
                toNotify,
                writableDb.delete(
                        MovieGenreContract.TABLE_NAME,
                        selection == null ? newSelection : selection + " AND " + newSelection,
                        newSelectionArgs));
    }

    @NonNull protected Set<Uri> getNotifyUris(
            @NonNull SQLiteDatabase readableDatabase,
            @Nullable  String selection,
            @Nullable String[] selectionArgs)
    {
        Set<Uri> set = new HashSet<>();
        set.add(movieGenreEntityContentUri);
        Cursor cursor = null;
        try
        {
            cursor = readableDatabase.query(
                    MovieGenreContract.TABLE_NAME,
                    new String[]{MovieGenreContract.COLUMN_GENRE_ID, MovieGenreContract.COLUMN_MOVIE_ID},
                    selection,
                    selectionArgs,
                    null, null, null);
            if (cursor != null && cursor.moveToFirst())
            {
                int indexGenre = cursor.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID);
                int indexMovie = cursor.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID);
                do
                {
                    MovieId movieId = new MovieId(cursor.getInt(indexMovie));
                    set.add(buildMovieGenreLocation(new GenreId(cursor.getInt(indexGenre)), movieId));
                    set.add(buildMovieGenreLocation(movieId));
                }
                while (cursor.moveToNext());
            }
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
        return set;
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
            case MOVIE_GENRES:
                return updateMovieGenres(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);

            case MOVIE_BY_ID_GENRES:
                return updateMovieGenreByMovieId(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);

            case GENRE_BY_ID_MOVIES:
                return updateMovieGenreByGenreId(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);

            case GENRE_BY_ID_MOVIE_BY_ID:
                return updateMovieGenreByIds(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @NonNull public NotificationListWithCount updateMovieGenres(
            @NonNull SQLiteDatabase writableDb,
            Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        Set<Uri> toNotify = new HashSet<>();
        toNotify.add(uri);
        toNotify.addAll(getNotifyUris(
                writableDb,
                selection,
                selectionArgs));
        return new NotificationListWithCount(
                toNotify,
                writableDb.update(MovieGenreContract.TABLE_NAME,
                        values, selection, selectionArgs));
    }

    @NonNull public NotificationListWithCount updateMovieGenreByMovieId(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getMovieId(uri);
        String newSelection = MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_MOVIE_ID + "=?";
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
        Set<Uri> toNotify = new HashSet<>();
        toNotify.add(uri);
        toNotify.addAll(getNotifyUris(
                writableDb,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs));
        return new NotificationListWithCount(
                toNotify,
                writableDb.update(
                MovieGenreContract.TABLE_NAME,
                values,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs));
    }

    @NonNull public NotificationListWithCount updateMovieGenreByGenreId(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getGenreId(uri);
        String newSelection = MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_GENRE_ID + "=?";
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
        Set<Uri> toNotify = new HashSet<>();
        toNotify.add(uri);
        toNotify.addAll(getNotifyUris(
                writableDb,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs));
        return new NotificationListWithCount(
                toNotify,
                writableDb.update(
                MovieGenreContract.TABLE_NAME,
                values,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs));
    }

    @NonNull public NotificationListWithCount updateMovieGenreByIds(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String[] ids = getMovieGenreId(uri);
        String newSelection = MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_GENRE_ID + "=?" +
                " AND " + MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_MOVIE_ID + "=?";
        String[] newSelectionArgs = new String[selectionArgs == null ? 2 : selectionArgs.length + 2];
        int i = 0;
        if (selectionArgs != null)
        {
            for (i = 0; i < selectionArgs.length; i++)
            {
                newSelectionArgs[i] = selectionArgs[i];
            }
        }
        newSelectionArgs[i++] = ids[0];
        newSelectionArgs[i] = ids[1];
        Set<Uri> toNotify = new HashSet<>();
        toNotify.add(uri);
        toNotify.addAll(getNotifyUris(
                writableDb,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs));
        return new NotificationListWithCount(
                toNotify,
                writableDb.update(
                MovieGenreContract.TABLE_NAME,
                values,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs));
    }
}
