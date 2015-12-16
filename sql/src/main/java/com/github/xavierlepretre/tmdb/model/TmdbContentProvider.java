package com.github.xavierlepretre.tmdb.model;

import com.github.xavierlepretre.tmdb.model.notify.NotificationListInsert;
import com.github.xavierlepretre.tmdb.model.notify.NotificationListWithCount;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.Collection;

public class TmdbContentProvider extends ContentProvider
{
    static final String DATABASE_NAME = "tmdb.db";
    static final int DATABASE_VERSION = 1;

    // The URI Matcher used by this content provider to find which helper to delegate to.
    @NonNull private final UriMatcher uriMatcher;
    @NonNull private final SparseArray<EntityProviderDelegate> providerDelegates;
    @NonNull private final SparseArray<EntitySQLHelperDelegate> helperDelegates;
    private TmdbDbHelper openHelper;

    public TmdbContentProvider()
    {
        this(new TmdbUriMatcherFactory(),
                new EntityProviderDelegateFactory(),
                new EntitySQLHelperDelegateFactory());
    }

    public TmdbContentProvider(
            @NonNull TmdbUriMatcherFactory uriMatcherFactory,
            @NonNull EntityProviderDelegateFactory delegateFactory,
            @NonNull EntitySQLHelperDelegateFactory helperDelegateFactory)
    {
        this(uriMatcherFactory, delegateFactory.createProviders(), helperDelegateFactory.createHelpers());
    }

    public TmdbContentProvider(
            @NonNull TmdbUriMatcherFactory uriMatcherFactory,
            @NonNull SparseArray<EntityProviderDelegate> providerDelegates,
            @NonNull SparseArray<EntitySQLHelperDelegate> helperDelegates)
    {
        this(uriMatcherFactory.create(providerDelegates),
                providerDelegates,
                helperDelegates);
    }

    public TmdbContentProvider(
            @NonNull UriMatcher uriMatcher,
            @NonNull SparseArray<EntityProviderDelegate> providerDelegates,
            @NonNull SparseArray<EntitySQLHelperDelegate> helperDelegates)
    {
        super();
        this.uriMatcher = uriMatcher;
        this.providerDelegates = providerDelegates;
        this.helperDelegates = helperDelegates;
    }

    @Override public boolean onCreate()
    {
        openHelper = createHelper(getContext());
        return true;
    }

    @NonNull protected TmdbDbHelper createHelper(@Nullable Context context)
    {
        return new TmdbDbHelper(context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION,
                helperDelegates);
    }

    @NonNull private SQLiteDatabase getWritableDatabase()
    {
        SQLiteDatabase writableDb = openHelper.getWritableDatabase();
        writableDb.execSQL("PRAGMA foreign_keys = ON;");
        return writableDb;
    }

    private void notifyResolver(@NonNull Collection<Uri> uris)
    {
        Context context = getContext();
        if (context == null)
        {
            return;
        }
        for (Uri uri : uris)
        {
            context.getContentResolver().notifyChange(uri, null);
        }
    }

    @Nullable @Override public String getType(@NonNull Uri uri)
    {
        int match = uriMatcher.match(uri);
        EntityProviderDelegate helper = providerDelegates.get(match);
        if (helper == null)
        {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return helper.getType(uri);
    }

    @Nullable @Override public Cursor query(
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String sortOrder)
    {
        int match = uriMatcher.match(uri);
        EntityProviderDelegate helper = providerDelegates.get(match);
        if (helper == null)
        {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return helper.query(
                openHelper.getReadableDatabase(),
                uri,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                null);
    }

    @Nullable @Override public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        int match = uriMatcher.match(uri);
        EntityProviderDelegate helper = providerDelegates.get(match);
        if (helper == null)
        {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        NotificationListInsert inserted = helper.insert(getWritableDatabase(), uri, values);
        notifyResolver(inserted.getToNotify());
        return inserted.getInserted();
    }

    @Override public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values)
    {
        int match = uriMatcher.match(uri);
        EntityProviderDelegate helper = providerDelegates.get(match);
        if (helper == null)
        {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        NotificationListWithCount inserted = helper.bulkInsert(getWritableDatabase(), uri, values);
        notifyResolver(inserted.getToNotify());
        return inserted.getCount();
    }

    @Override public int delete(
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        int match = uriMatcher.match(uri);
        EntityProviderDelegate helper = providerDelegates.get(match);
        if (helper == null)
        {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        NotificationListWithCount deleted = helper.delete(
                getWritableDatabase(),
                uri,
                selection,
                selectionArgs);
        notifyResolver(deleted.getToNotify());
        return deleted.getCount();
    }

    @Override public int update(
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        int match = uriMatcher.match(uri);
        EntityProviderDelegate helper = providerDelegates.get(match);
        if (helper == null)
        {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        NotificationListWithCount updated = helper.update(
                getWritableDatabase(),
                uri,
                values,
                selection,
                selectionArgs);
        notifyResolver(updated.getToNotify());
        return updated.getCount();
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override public void shutdown()
    {
        openHelper.close();
        super.shutdown();
    }
}
