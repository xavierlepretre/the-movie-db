package com.github.xavierlepretre.tmdb.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

public class TmdbContentProvider extends ContentProvider
{
    static final String DATABASE_NAME = "tmdb.db";
    static final int DATABASE_VERSION = 1;

    // The URI Matcher used by this content provider to find which helper to delegate to.
    @NonNull private final UriMatcher uriMatcher;
    @NonNull private final SparseArray<EntityProviderDelegate> providerDelegates;
    private TmdbDbHelper openHelper;

    public TmdbContentProvider()
    {
        this(new TmdbUriMatcherFactory(),
                new EntityProviderDelegateFactory());
    }

    public TmdbContentProvider(
            @NonNull TmdbUriMatcherFactory uriMatcherFactory,
            @NonNull EntityProviderDelegateFactory delegateFactory)
    {
        this(uriMatcherFactory, delegateFactory.createProviders());
    }

    public TmdbContentProvider(
            @NonNull TmdbUriMatcherFactory uriMatcherFactory,
            @NonNull SparseArray<EntityProviderDelegate> providerDelegates)
    {
        this(uriMatcherFactory.create(providerDelegates),
                providerDelegates);
    }

    public TmdbContentProvider(
            @NonNull UriMatcher uriMatcher,
            @NonNull SparseArray<EntityProviderDelegate> providerDelegates)
    {
        super();
        this.uriMatcher = uriMatcher;
        this.providerDelegates = providerDelegates;
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
                providerDelegates);
    }

    private void notifyResolver(@Nullable Uri uri)
    {
        if (uri == null)
        {
            return;
        }
        Context context = getContext();
        if (context == null)
        {
            return;
        }
        context.getContentResolver().notifyChange(uri, null);
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
        Uri inserted = helper.insert(openHelper.getWritableDatabase(), uri, values);
        notifyResolver(inserted);
        return inserted;
    }

    @Override public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values)
    {
        int match = uriMatcher.match(uri);
        EntityProviderDelegate helper = providerDelegates.get(match);
        if (helper == null)
        {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        int inserted = helper.bulkInsert(openHelper.getWritableDatabase(), uri, values);
        if (inserted > 0)
        {
            notifyResolver(uri);
        }
        return inserted;
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
        int deleted = helper.delete(
                openHelper.getWritableDatabase(),
                uri,
                selection,
                selectionArgs);
        if (deleted > 0)
        {
            notifyResolver(uri);
        }
        return deleted;
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
        int updated = helper.update(
                openHelper.getWritableDatabase(),
                uri,
                values,
                selection,
                selectionArgs);
        if (updated > 0)
        {
            notifyResolver(uri);
        }
        return updated;
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
