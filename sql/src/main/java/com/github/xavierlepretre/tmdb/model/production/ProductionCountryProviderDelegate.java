package com.github.xavierlepretre.tmdb.model.production;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.EntityProviderDelegate;
import com.neovisionaries.i18n.CountryCode;

public class ProductionCountryProviderDelegate implements EntityProviderDelegate
{
    private static final int PRODUCTION_COUNTRIES = 100;
    private static final int PRODUCTION_COUNTRY_BY_ID = 101;
    private static final int INDEX_INSERT_RESOLVE_CONFLICT_REPLACE = 5;

    @NonNull private final String contentAuthority;
    @NonNull private final Uri entityContentUri;
    @NonNull private final String contentDirType;
    @NonNull private final String contentItemType;
    @NonNull private final UriMatcher uriMatcher;

    public ProductionCountryProviderDelegate(
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
        uriMatcher.addURI(contentAuthority, ProductionCountryContract.PATH, PRODUCTION_COUNTRIES);
        uriMatcher.addURI(contentAuthority, ProductionCountryContract.PATH + "/*", PRODUCTION_COUNTRY_BY_ID);
    }

    @Override @NonNull public String getCreateQuery()
    {
        return "CREATE TABLE " + ProductionCountryContract.TABLE_NAME + "("
                + ProductionCountryContract._ID + " CHARACTER(2) PRIMARY KEY NOT NULL,"
                + ProductionCountryContract.COLUMN_NAME + " TEXT NULL"
                + ");";
    }

    @NonNull @Override public String getUpgradeQuery(int oldVersion, int newVersion)
    {
        return "DROP TABLE IF EXISTS " + ProductionCountryContract.TABLE_NAME + ";";
    }

    @Override public void registerWith(@NonNull UriMatcher uriMatcher, int outsideMatch)
    {
        uriMatcher.addURI(contentAuthority, ProductionCountryContract.PATH, outsideMatch);
        uriMatcher.addURI(contentAuthority, ProductionCountryContract.PATH + "/*", outsideMatch);
    }

    @Override @Nullable public String getType(@NonNull Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case PRODUCTION_COUNTRIES:
                return contentDirType;
            case PRODUCTION_COUNTRY_BY_ID:
                String id = getProductionCountryId(uri);
                if (CountryCode.getByCode(id) != null)
                {
                    return contentItemType;
                }
                // Fall through.
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
            case PRODUCTION_COUNTRIES:
                return getAllProductionCountrys(readableDb,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        having,
                        sortOrder,
                        limit);
            case PRODUCTION_COUNTRY_BY_ID:
                return getProductionCountryById(readableDb,
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

    @Nullable public Cursor getAllProductionCountrys(
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
        queryBuilder.setTables(ProductionCountryContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @Nullable public Cursor getProductionCountryById(
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
        String id = getProductionCountryId(uri);
        String newSelection = ProductionCountryContract.TABLE_NAME + "." + ProductionCountryContract._ID + "=?";
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
        queryBuilder.setTables(ProductionCountryContract.TABLE_NAME);
        return queryBuilder.query(readableDb,
                projection,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs,
                groupBy,
                having,
                sortOrder,
                limit);
    }

    @NonNull public String getProductionCountryId(@NonNull Uri uri)
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
            case PRODUCTION_COUNTRIES:
                long id = writableDb.insertWithOnConflict(
                        ProductionCountryContract.TABLE_NAME,
                        null,
                        values,
                        INDEX_INSERT_RESOLVE_CONFLICT_REPLACE);
                if (id <= 0)
                {
                    return null;
                }
                //noinspection ConstantConditions as it should have failed earlier
                return buildProductionCountryLocation(values.getAsString(ProductionCountryContract._ID));
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override public int bulkInsert(
            @NonNull SQLiteDatabase writableDb, @NonNull Uri uri, @NonNull ContentValues[] values)
    {
        switch (uriMatcher.match(uri))
        {
            case PRODUCTION_COUNTRIES:
                writableDb.beginTransaction();
                int returnCount = 0;
                long insertedId;
                try
                {
                    for (ContentValues value : values)
                    {
                        if (value != null && value.getAsString(ProductionCountryContract._ID) == null)
                        {
                            continue;
                        }
                        insertedId = writableDb.insert(ProductionCountryContract.TABLE_NAME, null, value);
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

    @NonNull public Uri buildProductionCountryLocation(@NonNull String id)
    {
        return buildProductionCountryLocation(entityContentUri, id);
    }

    @NonNull
    public static Uri buildProductionCountryLocation(@NonNull Uri entityContentUri, @NonNull String id)
    {
        return entityContentUri.buildUpon().appendPath(id).build();
    }

    @Override public int delete(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        switch (uriMatcher.match(uri))
        {
            case PRODUCTION_COUNTRIES:
                return deleteProductionCountrys(writableDb,
                        selection,
                        selectionArgs);
            case PRODUCTION_COUNTRY_BY_ID:
                return deleteProductionCountryById(writableDb,
                        uri,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public int deleteProductionCountrys(
            @NonNull SQLiteDatabase writableDb,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return writableDb.delete(ProductionCountryContract.TABLE_NAME, selection, selectionArgs);
    }

    public int deleteProductionCountryById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getProductionCountryId(uri);
        String newSelection = ProductionCountryContract.TABLE_NAME + "." + ProductionCountryContract._ID + "=?";
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
                ProductionCountryContract.TABLE_NAME,
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
            case PRODUCTION_COUNTRIES:
                return updateProductionCountrys(writableDb,
                        values,
                        selection,
                        selectionArgs);
            case PRODUCTION_COUNTRY_BY_ID:
                return updateProductionCountryById(writableDb,
                        uri,
                        values,
                        selection,
                        selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public int updateProductionCountrys(
            @NonNull SQLiteDatabase writableDb,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return writableDb.update(ProductionCountryContract.TABLE_NAME,
                values, selection, selectionArgs);
    }

    public int updateProductionCountryById(
            @NonNull SQLiteDatabase writableDb,
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        String id = getProductionCountryId(uri);
        String newSelection = ProductionCountryContract.TABLE_NAME + "." + ProductionCountryContract._ID + "=?";
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
                ProductionCountryContract.TABLE_NAME,
                values,
                selection == null ? newSelection : selection + " AND " + newSelection,
                newSelectionArgs);
    }
}
