package com.github.xavierlepretre.tmdb.model.production;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class ProductionCountryProviderDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.productionCountry.db";
    private ProductionCountryProviderDelegate providerDelegate;
    private ProductionCountrySQLiteOpenHelper sqlHelper;

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = spy(new ProductionCountryProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/productionCountry"),
                "dir_type",
                "item_type"));
        sqlHelper = new ProductionCountrySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1);
    }

    @After
    public void tearDown() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
    }

    @Test
    public void createRequestIsCorrect() throws Exception
    {
        assertThat(providerDelegate.getCreateQuery()).isEqualTo(
                "CREATE TABLE productionCountry(_id CHARACTER(2) PRIMARY KEY NOT NULL,name TEXT NULL);");
    }

    @Test
    public void upgradeRequestIsCorrect() throws Exception
    {
        assertThat(providerDelegate.getUpgradeQuery(1, 2)).isEqualTo(
                "DROP TABLE IF EXISTS productionCountry;"
        );
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
        assertThat(matcher.match(Uri.parse("content://content_authority/productionCountry"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/productionCountry/a"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/productionCountry/456"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/productionCountry/456/t"))).isEqualTo(UriMatcher.NO_MATCH);
    }

    @Test
    public void returnsProperTypes() throws Exception
    {
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/productionCountry"))).isEqualTo("dir_type");
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/productionCountry/GB"))).isEqualTo("item_type");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_mayFail() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/fake"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void properTypesFailsOnUnknownPath() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/productionCountry/FAKE"));
    }

    @Test
    public void getProductionCountryFromPath_isOk() throws Exception
    {
        assertThat(providerDelegate.getProductionCountryId(Uri.parse("content://content_authority/productionCountry/1"))).isEqualTo("1");
        assertThat(providerDelegate.getProductionCountryId(Uri.parse("content://content_authority/productionCountry/a"))).isEqualTo("a");
        assertThat(providerDelegate.getProductionCountryId(Uri.parse("content://anything/productionCountry/a"))).isEqualTo("a");
        assertThat(providerDelegate.getProductionCountryId(Uri.parse("content://anything/fake/a"))).isEqualTo("a");
        assertThat(providerDelegate.getProductionCountryId(Uri.parse("content://anything/fake/2"))).isEqualTo("2");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getProductionCountryFromPath_canFail() throws Exception
    {
        providerDelegate.getProductionCountryId(Uri.parse("content://content_authority/productionCountry"));
    }

    @Test
    public void buildProductionCountryLocation_isOk() throws Exception
    {
        assertThat(ProductionCountryProviderDelegate.buildProductionCountryLocation(
                Uri.parse("content://something"), "GB"))
                .isEqualTo(Uri.parse("content://something/GB"));
        assertThat(providerDelegate.buildProductionCountryLocation("US"))
                .isEqualTo(Uri.parse("content://content_authority/productionCountry/US"));
    }

    @Test(expected = SQLiteException.class)
    public void insertNull_fails() throws Exception
    {
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withMissingId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withNullId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, (String) null);
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);
    }

    @Test
    public void insertPutsInDb_andCanQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/productionCountry/GB");

        Cursor myProductionCountry = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myProductionCountry).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCountry.moveToFirst()).isTrue();
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract._ID)))
                .isEqualTo("GB");
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract.COLUMN_NAME)))
                .isEqualTo("United Kingdom");
    }

    @Test
    public void insertTwiceTheSame_replaces() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/productionCountry/GB");

        values.put(ProductionCountryContract.COLUMN_NAME, "Royaume-Uni");
        //noinspection ConstantConditions
        Uri replaced = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);
        //noinspection ConstantConditions
        assertThat(replaced.toString()).isEqualTo("content://content_authority/productionCountry/GB");

        Cursor myProductionCountry = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myProductionCountry).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCountry.moveToFirst()).isTrue();
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract._ID)))
                .isEqualTo("GB");
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract.COLUMN_NAME)))
                .isEqualTo("Royaume-Uni");
    }

    @Test
    public void canInsertWithNullName() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");

        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/productionCountry/GB");

        Cursor myProductionCountry = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myProductionCountry).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCountry.moveToFirst()).isTrue();
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract._ID)))
                .isEqualTo("GB");
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract.COLUMN_NAME)))
                .isNull();
    }

    @Test
    public void bulkInsert_withMissingId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCountryContract._ID, "US");
        value2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values))
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withNullId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCountryContract._ID, (String) null);
        value1.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCountryContract._ID, "US");
        value2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values))
                .isEqualTo(1);
    }

    @Test
    public void bulkInsertInDb_andCanQuery() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCountryContract._ID, "GB");
        value1.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCountryContract._ID, "US");
        value2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        ContentValues[] values = new ContentValues[]{value1, value2};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values))
                .isEqualTo(2);

        Cursor myProductionCountry = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                null, null, null, null, null, null, null);

        assertThat(myProductionCountry).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCountry.moveToFirst()).isTrue();
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract._ID)))
                .isEqualTo("GB");
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract.COLUMN_NAME)))
                .isEqualTo("United Kingdom");
        assertThat(myProductionCountry.moveToNext()).isTrue();
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract._ID)))
                .isEqualTo("US");
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract.COLUMN_NAME)))
                .isEqualTo("United States of America");
        assertThat(myProductionCountry.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertEmptyInDb() throws Exception
    {
        ContentValues[] values = new ContentValues[0];
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values))
                .isEqualTo(0);

        Cursor myProductionCountry = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                null, null, null, null, null, null, null);

        assertThat(myProductionCountry).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCountry.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertDuplicate_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(ProductionCountryContract._ID, "GB");
        value1.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        ContentValues value2 = new ContentValues();
        value2.put(ProductionCountryContract._ID, "US");
        value2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        ContentValues value3 = new ContentValues();
        value3.put(ProductionCountryContract._ID, "GB");
        value3.put(ProductionCountryContract.COLUMN_NAME, "Royaume Uni");
        ContentValues[] values = new ContentValues[]{value1, value2, value3};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values))
                .isEqualTo(2);

        Cursor myProductionCountry = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                null, null, null, null, null, null, null);

        assertThat(myProductionCountry).isNotNull();
        //noinspection ConstantConditions
        assertThat(myProductionCountry.moveToFirst()).isTrue();
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract._ID)))
                .isEqualTo("GB");
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract.COLUMN_NAME)))
                .isEqualTo("United Kingdom");
        assertThat(myProductionCountry.moveToNext()).isTrue();
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract._ID)))
                .isEqualTo("US");
        assertThat(myProductionCountry.getString(myProductionCountry.getColumnIndex(ProductionCountryContract.COLUMN_NAME)))
                .isEqualTo("United States of America");
        assertThat(myProductionCountry.moveToNext()).isFalse();
    }

    @Test
    public void queryListFromDb_isOk() throws Exception
    {
        ContentValues values1 = new ContentValues();
        values1.put(ProductionCountryContract._ID, "GB");
        values1.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(ProductionCountryContract._ID, "US");
        values2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                null, null, null, null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID)))
                .isEqualTo("GB");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME)))
                .isEqualTo("United Kingdom");
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID)))
                .isEqualTo("US");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME)))
                .isEqualTo("United States of America");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryListFromDbWithSelection_isOk() throws Exception
    {
        ContentValues values1 = new ContentValues();
        values1.put(ProductionCountryContract._ID, "GB");
        values1.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(ProductionCountryContract._ID, "US");
        values2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                null,
                ProductionCountryContract.COLUMN_NAME + "=?",
                new String[]{"United States of America"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID)))
                .isEqualTo("US");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME)))
                .isEqualTo("United States of America");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                null,
                ProductionCountryContract.COLUMN_NAME + "=?",
                new String[]{"United Kingdom"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID)))
                .isEqualTo("GB");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME)))
                .isEqualTo("United Kingdom");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithContradictorySelection_returnsEmpty() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                null,
                ProductionCountryContract.COLUMN_NAME + "=?",
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
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                null, null)).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
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
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                ProductionCountryContract.COLUMN_NAME + "=?",
                new String[]{"United Kingdom"})).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
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
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                ProductionCountryContract.COLUMN_NAME + "=?",
                new String[]{"United States of America"})).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
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
        values1.put(ProductionCountryContract._ID, "GB");
        values1.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(ProductionCountryContract._ID, "US");
        values2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values2);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                ProductionCountryContract.COLUMN_NAME + "=?",
                new String[]{"United States of America"})).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
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
        values1.put(ProductionCountryContract._ID, "GB");
        values1.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(ProductionCountryContract._ID, "US");
        values2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values2);
        ContentValues values3 = new ContentValues();
        values3.put(ProductionCountryContract._ID, "FR");
        values3.put(ProductionCountryContract.COLUMN_NAME, "France");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values3);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                ProductionCountryContract.COLUMN_NAME + " LIKE ?",
                new String[]{"United%"})).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
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
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCountryContract.COLUMN_NAME, "Royaume Uni");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                newValues,
                null, null)).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID))).isEqualTo("GB");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME))).isEqualTo("Royaume Uni");
    }

    @Test
    public void updateItemWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCountryContract.COLUMN_NAME, "Royaume-Uni");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                newValues,
                ProductionCountryContract.COLUMN_NAME + "=?",
                new String[]{"United Kingdom"})).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID))).isEqualTo("GB");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME))).isEqualTo("Royaume-Uni");
    }

    @Test
    public void updateItemWithChangedId_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCountryContract._ID, "US");
        newValues.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                newValues,
                ProductionCountryContract.COLUMN_NAME + "=?",
                new String[]{"United Kingdom"})).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/US"),
                null,
                null,
                null,
                null, null, null, null);
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID))).isEqualTo("US");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME))).isEqualTo("United States of America");
    }

    @Test
    public void updateItemWithContradictorySelection_doesNotUpdate() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCountryContract.COLUMN_NAME, "Royaume-Uni");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                newValues,
                ProductionCountryContract.COLUMN_NAME + "=?",
                new String[]{"United States of America"})).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID))).isEqualTo("GB");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME))).isEqualTo("United Kingdom");
    }

    @Test
    public void updateList_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);
        ContentValues values2 = new ContentValues();
        values2.put(ProductionCountryContract._ID, "US");
        values2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values2);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCountryContract.COLUMN_NAME, "Country");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                newValues,
                null,
                null)).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID))).isEqualTo("GB");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME))).isEqualTo("Country");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/US"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID))).isEqualTo("US");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME))).isEqualTo("Country");
    }

    @Test
    public void updateListWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ProductionCountryContract._ID, "GB");
        values.put(ProductionCountryContract.COLUMN_NAME, "United Kingdom");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values);
        ContentValues values2 = new ContentValues();
        values2.put(ProductionCountryContract._ID, "US");
        values2.put(ProductionCountryContract.COLUMN_NAME, "United States of America");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values2);
        ContentValues values3 = new ContentValues();
        values3.put(ProductionCountryContract._ID, "FR");
        values3.put(ProductionCountryContract.COLUMN_NAME, "France");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                values3);

        ContentValues newValues = new ContentValues();
        newValues.put(ProductionCountryContract.COLUMN_NAME, "Country");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/productionCountry"),
                newValues,
                ProductionCountryContract.TABLE_NAME + "." + ProductionCountryContract.COLUMN_NAME + " LIKE ?",
                new String[]{"United%"})).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/GB"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID))).isEqualTo("GB");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME))).isEqualTo("Country");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/US"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID))).isEqualTo("US");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME))).isEqualTo("Country");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/productionCountry/FR"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract._ID))).isEqualTo("FR");
        assertThat(found.getString(found.getColumnIndex(ProductionCountryContract.COLUMN_NAME))).isEqualTo("France");
    }
}