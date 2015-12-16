package com.github.xavierlepretre.tmdb.model.conf;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract.ImagesConfSegment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class ConfigurationProviderDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.configuration.db";
    private ConfigurationProviderDelegate providerDelegate;
    private EntitySQLiteOpenHelper sqlHelper;

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = spy(new ConfigurationProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/configuration"),
                "item_type"));
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new ConfigurationSQLHelperDelegate());
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
        assertThat(matcher.match(Uri.parse("content://content_authority/configuration"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/configuration/"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/configuration/a"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/configuration/456"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/configuration/456/t"))).isEqualTo(UriMatcher.NO_MATCH);
    }

    @Test
    public void returnsProperTypes() throws Exception
    {
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/configuration"))).isEqualTo("item_type");
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/configuration/"))).isEqualTo("item_type");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_mayFail() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/fake"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void properTypesFailsOnUnknownPath() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/configuration/a"));
    }

    @Test()
    public void insertNull_putsInDb() throws Exception
    {
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                null).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/configuration");

        Cursor myConfiguration = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myConfiguration).isNotNull();
        //noinspection ConstantConditions
        assertThat(myConfiguration.moveToFirst()).isTrue();
        assertThat(myConfiguration.getInt(myConfiguration.getColumnIndex(ConfigurationContract._ID)))
                .isEqualTo(ConfigurationContract.UNIQUE_ROW_ID);
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_BASE_URL)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_SECURE_BASE_URL)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_BACKDROP_SIZES)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_LOGO_SIZES)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_POSTER_SIZES)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_PROFILE_SIZES)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_STILL_SIZES)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ConfigurationContract.COLUMN_CHANGE_KEYS)))
                .isNull();
    }

    @Test
    public void insert_withMissingId_putsInDbAndCanQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/configuration");

        Cursor myConfiguration = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myConfiguration).isNotNull();
        //noinspection ConstantConditions
        assertThat(myConfiguration.moveToFirst()).isTrue();
        assertThat(myConfiguration.getInt(myConfiguration.getColumnIndex(ConfigurationContract._ID)))
                .isEqualTo(ConfigurationContract.UNIQUE_ROW_ID);
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_BASE_URL)))
                .isEqualTo("url1");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_SECURE_BASE_URL)))
                .isEqualTo("url2");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_BACKDROP_SIZES)))
                .isEqualTo("w1,w2");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_LOGO_SIZES)))
                .isEqualTo("w3,w4");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_POSTER_SIZES)))
                .isEqualTo("w5,w6");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_PROFILE_SIZES)))
                .isEqualTo("w7,w8");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_STILL_SIZES)))
                .isEqualTo("w9,w10");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ConfigurationContract.COLUMN_CHANGE_KEYS)))
                .isEqualTo("key1,key2");
    }


    @Test
    public void insert_withNullId_putsInDbAndCanQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ConfigurationContract._ID, (Integer) null);
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/configuration");

        Cursor myConfiguration = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myConfiguration).isNotNull();
        //noinspection ConstantConditions
        assertThat(myConfiguration.moveToFirst()).isTrue();
        assertThat(myConfiguration.getInt(myConfiguration.getColumnIndex(ConfigurationContract._ID)))
                .isEqualTo(ConfigurationContract.UNIQUE_ROW_ID);
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_BASE_URL)))
                .isEqualTo("url1");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_SECURE_BASE_URL)))
                .isEqualTo("url2");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_BACKDROP_SIZES)))
                .isEqualTo("w1,w2");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_LOGO_SIZES)))
                .isEqualTo("w3,w4");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_POSTER_SIZES)))
                .isEqualTo("w5,w6");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_PROFILE_SIZES)))
                .isEqualTo("w7,w8");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_STILL_SIZES)))
                .isEqualTo("w9,w10");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ConfigurationContract.COLUMN_CHANGE_KEYS)))
                .isEqualTo("key1,key2");
    }

    @Test
    public void insertTwiceTheSame_replaces() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/configuration");

        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url3");
        //noinspection ConstantConditions
        Uri replaced = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(replaced.toString()).isEqualTo("content://content_authority/configuration");

        Cursor myConfiguration = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myConfiguration).isNotNull();
        //noinspection ConstantConditions
        assertThat(myConfiguration.moveToFirst()).isTrue();
        assertThat(myConfiguration.getInt(myConfiguration.getColumnIndex(ConfigurationContract._ID)))
                .isEqualTo(ConfigurationContract.UNIQUE_ROW_ID);
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_BASE_URL)))
                .isEqualTo("url3");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_SECURE_BASE_URL)))
                .isEqualTo("url2");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_BACKDROP_SIZES)))
                .isEqualTo("w1,w2");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_LOGO_SIZES)))
                .isEqualTo("w3,w4");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_POSTER_SIZES)))
                .isEqualTo("w5,w6");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_PROFILE_SIZES)))
                .isEqualTo("w7,w8");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_STILL_SIZES)))
                .isEqualTo("w9,w10");
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ConfigurationContract.COLUMN_CHANGE_KEYS)))
                .isEqualTo("key1,key2");
    }

    @Test
    public void canInsertWithAllNull() throws Exception
    {
        ContentValues values = new ContentValues();

        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/configuration");

        Cursor myConfiguration = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myConfiguration).isNotNull();
        //noinspection ConstantConditions
        assertThat(myConfiguration.moveToFirst()).isTrue();
        assertThat(myConfiguration.getInt(myConfiguration.getColumnIndex(ConfigurationContract._ID)))
                .isEqualTo(ConfigurationContract.UNIQUE_ROW_ID);
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_BASE_URL)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_SECURE_BASE_URL)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_BACKDROP_SIZES)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_LOGO_SIZES)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_POSTER_SIZES)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_PROFILE_SIZES)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ImagesConfSegment.COLUMN_STILL_SIZES)))
                .isNull();
        assertThat(myConfiguration.getString(myConfiguration.getColumnIndex(ConfigurationContract.COLUMN_CHANGE_KEYS)))
                .isNull();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void bulkInsertInDb_fails() throws Exception
    {
        ContentValues value1 = new ContentValues();
        ContentValues value2 = new ContentValues();
        ContentValues[] values = new ContentValues[]{value1, value2};
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values);
    }

    @Test
    public void queryItemFromDbWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                null,
                ConfigurationContract.COLUMN_CHANGE_KEYS + "=?",
                new String[]{"key1,key2"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getInt(found.getColumnIndex(ConfigurationContract._ID)))
                .isEqualTo(ConfigurationContract.UNIQUE_ROW_ID);
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_BASE_URL)))
                .isEqualTo("url1");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_SECURE_BASE_URL)))
                .isEqualTo("url2");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_BACKDROP_SIZES)))
                .isEqualTo("w1,w2");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_LOGO_SIZES)))
                .isEqualTo("w3,w4");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_POSTER_SIZES)))
                .isEqualTo("w5,w6");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_PROFILE_SIZES)))
                .isEqualTo("w7,w8");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_STILL_SIZES)))
                .isEqualTo("w9,w10");
        assertThat(found.getString(found.getColumnIndex(ConfigurationContract.COLUMN_CHANGE_KEYS)))
                .isEqualTo("key1,key2");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithContradictorySelection_returnsEmpty() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                null,
                ConfigurationContract.COLUMN_CHANGE_KEYS + "=?",
                new String[]{"key2,key1"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);
    }

    @Test
    public void deleteItem_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                null, null).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/configuration"),
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
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                ConfigurationContract.COLUMN_CHANGE_KEYS + "=?",
                new String[]{"key1,key2"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/configuration"),
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
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                ConfigurationContract.COLUMN_CHANGE_KEYS + "=?",
                new String[]{"key2,key1"}).getCount()).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/configuration"),
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
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w6,w7");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                newValues,
                null, null).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getInt(found.getColumnIndex(ConfigurationContract._ID)))
                .isEqualTo(ConfigurationContract.UNIQUE_ROW_ID);
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_BASE_URL)))
                .isEqualTo("url1");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_SECURE_BASE_URL)))
                .isEqualTo("url2");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_BACKDROP_SIZES)))
                .isEqualTo("w6,w7");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_LOGO_SIZES)))
                .isEqualTo("w3,w4");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_POSTER_SIZES)))
                .isEqualTo("w5,w6");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_PROFILE_SIZES)))
                .isEqualTo("w7,w8");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_STILL_SIZES)))
                .isEqualTo("w9,w10");
        assertThat(found.getString(found.getColumnIndex(ConfigurationContract.COLUMN_CHANGE_KEYS)))
                .isEqualTo("key1,key2");
    }

    @Test
    public void updateItemWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w7,w8");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                newValues,
                ImagesConfSegment.COLUMN_SECURE_BASE_URL + "=?",
                new String[]{"url2"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getInt(found.getColumnIndex(ConfigurationContract._ID)))
                .isEqualTo(ConfigurationContract.UNIQUE_ROW_ID);
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_BASE_URL)))
                .isEqualTo("url1");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_SECURE_BASE_URL)))
                .isEqualTo("url2");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_BACKDROP_SIZES)))
                .isEqualTo("w7,w8");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_LOGO_SIZES)))
                .isEqualTo("w3,w4");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_POSTER_SIZES)))
                .isEqualTo("w5,w6");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_PROFILE_SIZES)))
                .isEqualTo("w7,w8");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_STILL_SIZES)))
                .isEqualTo("w9,w10");
        assertThat(found.getString(found.getColumnIndex(ConfigurationContract.COLUMN_CHANGE_KEYS)))
                .isEqualTo("key1,key2");
    }

    @Test
    public void updateItemWithContradictorySelection_doesNotUpdate() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w6,w7");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                newValues,
                ImagesConfSegment.COLUMN_SECURE_BASE_URL + "=?",
                new String[]{"url3"}).getCount()).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getInt(found.getColumnIndex(ConfigurationContract._ID)))
                .isEqualTo(ConfigurationContract.UNIQUE_ROW_ID);
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_BASE_URL)))
                .isEqualTo("url1");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_SECURE_BASE_URL)))
                .isEqualTo("url2");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_BACKDROP_SIZES)))
                .isEqualTo("w1,w2");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_LOGO_SIZES)))
                .isEqualTo("w3,w4");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_POSTER_SIZES)))
                .isEqualTo("w5,w6");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_PROFILE_SIZES)))
                .isEqualTo("w7,w8");
        assertThat(found.getString(found.getColumnIndex(ImagesConfSegment.COLUMN_STILL_SIZES)))
                .isEqualTo("w9,w10");
        assertThat(found.getString(found.getColumnIndex(ConfigurationContract.COLUMN_CHANGE_KEYS)))
                .isEqualTo("key1,key2");
    }
}