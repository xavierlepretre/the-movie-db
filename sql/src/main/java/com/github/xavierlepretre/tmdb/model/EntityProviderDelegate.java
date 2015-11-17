package com.github.xavierlepretre.tmdb.model;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface EntityProviderDelegate
{
    @NonNull String getCreateQuery();

    @NonNull String getUpgradeQuery(int oldVersion, int newVersion);

    void registerWith(@NonNull UriMatcher uriMatcher, int outsideMatch);

    @Nullable String getType(@NonNull Uri uri);

    @Nullable Cursor query(
            @NonNull SQLiteDatabase readableDb,
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String groupBy,
            @Nullable String having,
            @Nullable String sortOrder,
            @Nullable String limit);

    @Nullable Uri insert(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values);

    int bulkInsert(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @NonNull ContentValues[] values);

    int delete(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs);

    int update(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs);
}
