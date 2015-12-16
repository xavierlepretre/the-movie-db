package com.github.xavierlepretre.tmdb.model;

import com.github.xavierlepretre.tmdb.model.notify.NotificationListInsert;
import com.github.xavierlepretre.tmdb.model.notify.NotificationListWithCount;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface EntityProviderDelegate
{
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

    @NonNull NotificationListInsert insert(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values);

    @NonNull NotificationListWithCount bulkInsert(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @NonNull ContentValues[] values);

    @NonNull NotificationListWithCount delete(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs);

    @NonNull NotificationListWithCount update(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs);
}
