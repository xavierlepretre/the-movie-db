package com.github.xavierlepretre.tmdb.sql.production;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyId;
import com.github.xavierlepretre.tmdb.sql.EntityProviderDelegate;
import com.github.xavierlepretre.tmdb.sql.notify.NotificationListInsert;
import com.github.xavierlepretre.tmdb.sql.notify.NotificationListWithCount;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyContract;

import java.util.Collections;

public class ProductionCompanyProviderDelegate implements EntityProviderDelegate
{
    private static final int PRODUCTION_COMPANIES = 100;
    private static final int PRODUCTION_COMPANY_BY_ID = 101;
    private static final int INDEX_INSERT_RESOLVE_CONFLICT_REPLACE = 5;

    @NonNull private final String contentAuthority;
    @NonNull private final Uri entityContentUri;
    @NonNull private final String contentDirType;
    @NonNull private final String contentItemType;
    @NonNull private final UriMatcher uriMatcher;

    public ProductionCompanyProviderDelegate(
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
        uriMatcher.addURI(contentAuthority, ProductionCompanyContract.PATH, PRODUCTION_COMPANIES);
        uriMatcher.addURI(contentAuthority, ProductionCompanyContract.PATH + "/#", PRODUCTION_COMPANY_BY_ID);
    }

    @Override public void registerWith(@NonNull UriMatcher uriMatcher, int outsideMatch)
    {
        uriMatcher.addURI(contentAuthority, ProductionCompanyContract.PATH, outsideMatch);
        uriMatcher.addURI(contentAuthority, ProductionCompanyContract.PATH + "/#", outsideMatch);
    }

    @Override @Nullable public String getType(@NonNull Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case PRODUCTION_COMPANIES:
                return contentDirType;
            case PRODUCTION_COMPANY_BY_ID:
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
            case PRODUCTION_COMPANIES:
                return getAllProductionCompanies(readableDb,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sortOrder,
                        limit);
            case PRODUCTION_COMPANY_BY_ID:
                return getProductionCompanyById(readableDb,
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

    @Nullable public Cursor getAllProductionCompanies(
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
        queryBuilder.setTables(ProductionCompanyContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @Nullable public Cursor getProductionCompanyById(
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
        String id = getProductionCompanyId(uri);
        String newSelection = ProductionCompanyContract.TABLE_NAME + "." + ProductionCompanyContract._ID + "=?";
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
        queryBuilder.setTables(ProductionCompanyContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @NonNull public String getProductionCompanyId(@NonNull Uri uri)
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
            case PRODUCTION_COMPANIES:
                if (values != null && values.getAsLong(ProductionCompanyContract._ID) == null)
                {
                    throw new SQLiteConstraintException(ProductionCompanyContract._ID + " cannot be null");
                }
                long id = writableDb.insertWithOnConflict(
                        ProductionCompanyContract.TABLE_NAME,
                        null,
                        values,
                        INDEX_INSERT_RESOLVE_CONFLICT_REPLACE);
                if (id <= 0)
                {
                    return new NotificationListInsert(Collections.<Uri>emptyList(), null);
                }
                Uri inserted = buildProductionCompanyLocation(id);
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
            case PRODUCTION_COMPANIES:
                writableDb.beginTransaction();
                int returnCount = 0;
                long insertedId;
                try
                {
                    for (ContentValues value : values)
                    {
                        if (value == null || value.getAsLong(ProductionCompanyContract._ID) == null)
                        {
                            continue;
                        }
                        insertedId = writableDb.insertWithOnConflict(
                                ProductionCompanyContract.TABLE_NAME,
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

    @NonNull public Uri buildProductionCompanyLocation(@NonNull ProductionCompanyId id)
    {
        return buildProductionCompanyLocation(id.getId());
    }

    @NonNull private Uri buildProductionCompanyLocation(long id)
    {
        return buildProductionCompanyLocation(entityContentUri, id);
    }

    @NonNull public static Uri buildProductionCompanyLocation(
            @NonNull Uri entityContentUri,
            @NonNull ProductionCompanyId id)
    {
        return buildProductionCompanyLocation(entityContentUri, id.getId());
    }

    @NonNull private static Uri buildProductionCompanyLocation(
            @NonNull Uri entityContentUri,
            long id)
    {
        return entityContentUri.buildUpon().appendPath(Long.toString(id)).build();
    }

    @NonNull @Override public NotificationListWithCount delete(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        switch (uriMatcher.match(uri))
        {
            case PRODUCTION_COMPANIES:
                return deleteProductionCompanies(writableDb,
                        uri,
                        selection,
                        selectionArgs);
            case PRODUCTION_COMPANY_BY_ID:
                return deleteProductionCompanyById(writableDb,
                        uri,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @NonNull public NotificationListWithCount deleteProductionCompanies(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return new NotificationListWithCount(
                Collections.singleton(uri),
                writableDb.delete(
                        ProductionCompanyContract.TABLE_NAME,
                        selection,
                        selectionArgs));
    }

    @NonNull public NotificationListWithCount deleteProductionCompanyById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getProductionCompanyId(uri);
        String newSelection = ProductionCompanyContract.TABLE_NAME + "." + ProductionCompanyContract._ID + "=?";
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
                        ProductionCompanyContract.TABLE_NAME,
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
            case PRODUCTION_COMPANIES:
                return updateProductionCompanies(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);
            case PRODUCTION_COMPANY_BY_ID:
                return updateProductionCompanyById(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @NonNull public NotificationListWithCount updateProductionCompanies(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return new NotificationListWithCount(
                Collections.singleton(uri),
                writableDb.update(ProductionCompanyContract.TABLE_NAME,
                        values, selection, selectionArgs));
    }

    @NonNull public NotificationListWithCount updateProductionCompanyById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getProductionCompanyId(uri);
        String newSelection = ProductionCompanyContract.TABLE_NAME + "." + ProductionCompanyContract._ID + "=?";
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
                    ProductionCompanyContract.TABLE_NAME,
                    values,
                    selection == null ? newSelection : selection + " AND " + newSelection,
                    newSelectionArgs));
    }
}
