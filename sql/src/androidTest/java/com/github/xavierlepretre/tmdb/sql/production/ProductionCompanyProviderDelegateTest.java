package com.github.xavierlepretre.tmdb.sql.production;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.sql.EntitySQLiteOpenHelper;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyContract;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyId;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class ProductionCompanyProviderDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.productionCompany.db";
    private ProductionCompanyProviderDelegate providerDelegate;
    private EntitySQLiteOpenHelper sqlHelper;

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = spy(new ProductionCompanyProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/productionCompany"),
                "dir_type",
                "item_type"));
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new ProductionCompanySQLHelperDelegate());
    }

    @After
    public void tearDown() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
    }

    @Test
    public void registersWithExternalUriMatcher() throws Exception
    {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        int number = (int) System.nanoTime() & Integer.MAX_VALUE;
        providerDelegate.registerWith(matcher, number);
        assertThat(matcher.match(Uri.parse("content://no_match"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/not"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/productionCompany"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/productionCompany/a"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/productionCompany/456"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/productionCompany/456/t"))).isEqualTo(UriMatcher.NO_MATCH);
    }

    @Test
    public void returnsProperTypes() throws Exception
    {
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/productionCompany"))).isEqualTo("dir_type");
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/productionCompany/34"))).isEqualTo("item_type");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_mayFail() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/fake"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void properTypesFailsOnUnknownPath() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/productionCompany/a"));
    }

    @Test
    public void getProductionCompanyFromPath_isOk() throws Exception
    {
        assertThat(providerDelegate.getProductionCompanyId(Uri.parse("content://content_authority/productionCompany/1"))).isEqualTo("1");
        assertThat(providerDelegate.getProductionCompanyId(Uri.parse("content://content_authority/productionCompany/a"))).isEqualTo("a");
        assertThat(providerDelegate.getProductionCompanyId(Uri.parse("content://anything/productionCompany/a"))).isEqualTo("a");
        assertThat(providerDelegate.getProductionCompanyId(Uri.parse("content://anything/fake/a"))).isEqualTo("a");
        assertThat(providerDelegate.getProductionCompanyId(Uri.parse("content://anything/fake/2"))).isEqualTo("2");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getProductionCompanyFromPath_canFail() throws Exception
    {
        providerDelegate.getProductionCompanyId(Uri.parse("content://content_authority/productionCompany"));
    }

    @Test
    public void buildProductionCompanyLocation_isOk() throws Exception
    {
        assertThat(ProductionCompanyProviderDelegate.buildProductionCompanyLocation(
                Uri.parse("content://something"), new ProductionCompanyId(60)))
                .isEqualTo(Uri.parse("content://something/60"));
        assertThat(providerDelegate.buildProductionCompanyLocation(new ProductionCompanyId(870)))
                .isEqualTo(Uri.parse("content://content_authority/productionCompany/870"));
    }

    @Test(expected = SQLiteException.class)
    public void insertNull_fails() throws Exception
    {
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withMissingId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withNullId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, (Long) null);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);
    }

    @Test
    public void insertPutsInDb_andCanQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/productionCompany/5");

        Cursor myProductionCompany = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myProductionCompany).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCompany.moveToFirst()).isTrue();
        assertThat(myProductionCompany.getLong(myProductionCompany.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(5L);
        assertThat(myProductionCompany.getString(myProductionCompany.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isEqualTo("Columbia Pictures");
    }

    @Test
    public void insertTwiceTheSame_replaces() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/productionCompany/5");

        values.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        //noinspection ConstantConditions
        Uri replaced = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(replaced.toString()).isEqualTo("content://content_authority/productionCompany/5");

        Cursor myProductionCompany = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myProductionCompany).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCompany.moveToFirst()).isTrue();
        assertThat(myProductionCompany.getLong(myProductionCompany.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(5L);
        assertThat(myProductionCompany.getString(myProductionCompany.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isEqualTo("Danjaq");
    }

    @Test
    public void canInsertWithNullName() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);

        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/productionCompany/5");

        Cursor myProductionCompany = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myProductionCompany).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCompany.moveToFirst()).isTrue();
        assertThat(myProductionCompany.getLong(myProductionCompany.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(5L);
        assertThat(myProductionCompany.getString(myProductionCompany.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isNull();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void insertOnProductionCompanyById_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                values);
    }

    @Test
    public void bulkInsert_with1Null_skips() throws Exception
    {
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCompanyContract._ID, 6);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        ContentValues[] values = new ContentValues[]{null, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withMissingId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCompanyContract._ID, 6);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withNullId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCompanyContract._ID, (Long) null);
        value1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCompanyContract._ID, 6);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values).getCount())
                .isEqualTo(1);
    }
    
    @Test
    public void bulkInsertInDb_andCanQuery() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCompanyContract._ID, 5);
        value1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCompanyContract._ID, 6);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        ContentValues[] values = new ContentValues[]{value1, value2};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values).getCount())
                .isEqualTo(2);

        Cursor myProductionCompany = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                null, null, null, null, null, null, null);

        assertThat(myProductionCompany).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCompany.moveToFirst()).isTrue();
        assertThat(myProductionCompany.getLong(myProductionCompany.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(5L);
        assertThat(myProductionCompany.getString(myProductionCompany.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isEqualTo("Columbia Pictures");
        assertThat(myProductionCompany.moveToNext()).isTrue();
        assertThat(myProductionCompany.getLong(myProductionCompany.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(6L);
        assertThat(myProductionCompany.getString(myProductionCompany.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isEqualTo("Danjaq");
        assertThat(myProductionCompany.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertEmptyInDb() throws Exception
    {
        ContentValues[] values = new ContentValues[0];
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values).getCount())
                .isEqualTo(0);

        Cursor myProductionCompany = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                null, null, null, null, null, null, null);

        assertThat(myProductionCompany).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCompany.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertExisting_overwrites() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCompanyContract._ID, 5);
        value1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        assertThat(providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                value1).getInserted())
                .isEqualTo(Uri.parse("content://content_authority/productionCompany/5"));

        ContentValues value2 = new ContentValues();
        value2.put(ProductionCompanyContract._ID, 5);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Fox");
        ContentValues value3 = new ContentValues();
        value3.put(ProductionCompanyContract._ID, 6);
        value3.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        ContentValues[] values = new ContentValues[]{value1, value2, value3};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values).getCount())
                .isEqualTo(3);

        Cursor myProductionCompany = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                null, null, null, null, null, null, null);

        assertThat(myProductionCompany).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCompany.moveToFirst()).isTrue();
        assertThat(myProductionCompany.getLong(myProductionCompany.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(5L);
        assertThat(myProductionCompany.getString(myProductionCompany.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isEqualTo("Fox");
        assertThat(myProductionCompany.moveToNext()).isTrue();
        assertThat(myProductionCompany.getLong(myProductionCompany.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(6L);
        assertThat(myProductionCompany.getString(myProductionCompany.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isEqualTo("Danjaq");
        assertThat(myProductionCompany.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertDuplicate_lastWins() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCompanyContract._ID, 5);
        value1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCompanyContract._ID, 5);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Fox");
        ContentValues value3 = new ContentValues();
        value3.put(ProductionCompanyContract._ID, 6);
        value3.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        ContentValues[] values = new ContentValues[]{value1, value2, value3};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values).getCount())
                .isEqualTo(3);

        Cursor myProductionCompany = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                null, null, null, null, null, null, null);

        assertThat(myProductionCompany).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCompany.moveToFirst()).isTrue();
        assertThat(myProductionCompany.getLong(myProductionCompany.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(5L);
        assertThat(myProductionCompany.getString(myProductionCompany.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isEqualTo("Fox");
        assertThat(myProductionCompany.moveToNext()).isTrue();
        assertThat(myProductionCompany.getLong(myProductionCompany.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(6L);
        assertThat(myProductionCompany.getString(myProductionCompany.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isEqualTo("Danjaq");
        assertThat(myProductionCompany.moveToNext()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void bulkInsertOnProductionCompanyById_fails() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCompanyContract._ID, 5);
        value1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCompanyContract._ID, 6);
        value2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                new ContentValues[]{value1, value2});
    }

    @Test
    public void queryListFromDb_isOk() throws Exception
    {
        ContentValues values1 = new ContentValues();
        values1.put(ProductionCompanyContract._ID, 5);
        values1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(ProductionCompanyContract._ID, 6);
        values2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                null, null, null, null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(5L);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isEqualTo("Columbia Pictures");
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(6L);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isEqualTo("Danjaq");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryListFromDbWithSelection_isOk() throws Exception
    {
        ContentValues values1 = new ContentValues();
        values1.put(ProductionCompanyContract._ID, 5);
        values1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(ProductionCompanyContract._ID, 6);
        values2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                null,
                ProductionCompanyContract.COLUMN_NAME + "=?",
                new String[]{"Danjaq"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(6L);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isEqualTo("Danjaq");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                null,
                ProductionCompanyContract.COLUMN_NAME + "=?",
                new String[]{"Columbia Pictures"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID)))
                .isEqualTo(5L);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME)))
                .isEqualTo("Columbia Pictures");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithContradictorySelection_returnsEmpty() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                null,
                ProductionCompanyContract.COLUMN_NAME + "=?",
                new String[]{"Danjaq"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);
    }

    @Test
    public void deleteItem_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                null, null).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                null,
                null,
                null,
                null, null, null, null);
        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);
    }

    @Test
    public void deleteItemWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                ProductionCompanyContract.COLUMN_NAME + "=?",
                new String[]{"Columbia Pictures"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                null,
                null,
                null,
                null, null, null, null);
        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);
    }

    @Test
    public void deleteItemWithContradictorySelection_doesNotDelete() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                ProductionCompanyContract.COLUMN_NAME + "=?",
                new String[]{"Danjaq"}).getCount()).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                null,
                null,
                null,
                null, null, null, null);
        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
    }

    @Test
    public void deleteItemWithContradictorySelection_doesNotDeleteTheOtherOne() throws Exception
    {
        ContentValues values1 = new ContentValues();
        values1.put(ProductionCompanyContract._ID, 5);
        values1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(ProductionCompanyContract._ID, 6);
        values2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values2);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                ProductionCompanyContract.COLUMN_NAME + "=?",
                new String[]{"Danjaq"}).getCount()).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                null,
                null,
                null,
                null, null, null, null);
        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
    }

    @Test
    public void deleteList_isOk() throws Exception
    {
        ContentValues values1 = new ContentValues();
        values1.put(ProductionCompanyContract._ID, 5);
        values1.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(ProductionCompanyContract._ID, 6);
        values2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values2);
        ContentValues values3 = new ContentValues();
        values3.put(ProductionCompanyContract._ID, 7);
        values3.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values3);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                ProductionCompanyContract.COLUMN_NAME + " LIKE ?",
                new String[]{"Danj%"}).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                null,
                null,
                null,
                null, null, null, null);
        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
    }

    @Test
    public void updateItem_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                newValues,
                null, null).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID))).isEqualTo(5L);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME))).isEqualTo("Danjaq");
    }

    @Test
    public void updateItemWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                newValues,
                ProductionCompanyContract.COLUMN_NAME + "=?",
                new String[]{"Columbia Pictures"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID))).isEqualTo(5L);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME))).isEqualTo("Danjaq");
    }

    @Test
    public void updateItemWithChangedId_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCompanyContract._ID, 6);
        newValues.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                newValues,
                ProductionCompanyContract.COLUMN_NAME + "=?",
                new String[]{"Columbia Pictures"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/6"),
                null,
                null,
                null,
                null, null, null, null);
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID))).isEqualTo(6);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME))).isEqualTo("Danjaq");
    }

    @Test
    public void updateItemWithContradictorySelection_doesNotUpdate() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCompanyContract.COLUMN_NAME, "Fox");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                newValues,
                ProductionCompanyContract.COLUMN_NAME + "=?",
                new String[]{"Danjaq"}).getCount()).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID))).isEqualTo(5);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME))).isEqualTo("Columbia Pictures");
    }

    @Test
    public void updateItemWithMissingElement_doesNotLoseSavedInfo() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCompanyContract._ID, 5);
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                newValues,
                null, null).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID))).isEqualTo(5L);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME))).isEqualTo("Columbia Pictures");
    }

    @Test
    public void updateList_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);
        ContentValues values2 = new ContentValues();
        values2.put(ProductionCompanyContract._ID, 6);
        values2.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values2);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCompanyContract.COLUMN_NAME, "Fox");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                newValues,
                null,
                null).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID))).isEqualTo(5);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME))).isEqualTo("Fox");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/6"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID))).isEqualTo(6);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME))).isEqualTo("Fox");
    }

    @Test
    public void updateListWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCompanyContract._ID, 5);
        values.put(ProductionCompanyContract.COLUMN_NAME, "Columbia Pictures");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values);
        ContentValues values2 = new ContentValues();
        values2.put(ProductionCompanyContract._ID, 6);
        values2.put(ProductionCompanyContract.COLUMN_NAME, "Lucas");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values2);
        ContentValues values3 = new ContentValues();
        values3.put(ProductionCompanyContract._ID, 7);
        values3.put(ProductionCompanyContract.COLUMN_NAME, "Fox");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                values3);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCompanyContract.COLUMN_NAME, "Danjaq");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCompany"),
                newValues,
                ProductionCompanyContract.TABLE_NAME + "." + ProductionCompanyContract._ID + "<=?",
                new String[]{"6"}).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/5"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID))).isEqualTo(5);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME))).isEqualTo("Danjaq");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/6"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID))).isEqualTo(6);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME))).isEqualTo("Danjaq");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCompany/7"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(ProductionCompanyContract._ID))).isEqualTo(7);
        assertThat(found.getString(found.getColumnIndex(ProductionCompanyContract.COLUMN_NAME))).isEqualTo("Fox");
    }
}