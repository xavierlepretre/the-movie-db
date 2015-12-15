package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class CollectionProviderDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.collection.db";
    private CollectionProviderDelegate providerDelegate;
    private EntitySQLiteOpenHelper sqlHelper;

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = spy(new CollectionProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/collection"),
                "dir_type",
                "item_type"));
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new CollectionSQLHelperDelegate());
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
        assertThat(matcher.match(Uri.parse("content://content_authority/collection"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/collection/a"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/collection/456"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/collection/456/t"))).isEqualTo(UriMatcher.NO_MATCH);
    }

    @Test
    public void returnsProperTypes() throws Exception
    {
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/collection"))).isEqualTo("dir_type");
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/collection/34"))).isEqualTo("item_type");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_mayFail() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/fake"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void properTypesFailsOnUnknownPath() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/collection/a"));
    }

    @Test
    public void getCollectionFromPath_isOk() throws Exception
    {
        assertThat(providerDelegate.getCollectionId(Uri.parse("content://content_authority/collection/1"))).isEqualTo("1");
        assertThat(providerDelegate.getCollectionId(Uri.parse("content://content_authority/collection/a"))).isEqualTo("a");
        assertThat(providerDelegate.getCollectionId(Uri.parse("content://anything/collection/a"))).isEqualTo("a");
        assertThat(providerDelegate.getCollectionId(Uri.parse("content://anything/fake/a"))).isEqualTo("a");
        assertThat(providerDelegate.getCollectionId(Uri.parse("content://anything/fake/2"))).isEqualTo("2");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getCollectionFromPath_canFail() throws Exception
    {
        providerDelegate.getCollectionId(Uri.parse("content://content_authority/collection"));
    }

    @Test
    public void buildCollectionLocation_isOk() throws Exception
    {
        assertThat(CollectionProviderDelegate.buildCollectionLocation(
                Uri.parse("content://something"), new CollectionId(60)))
                .isEqualTo(Uri.parse("content://something/60"));
        assertThat(providerDelegate.buildCollectionLocation(new CollectionId(870)))
                .isEqualTo(Uri.parse("content://content_authority/collection/870"));
    }

    @Test(expected = SQLiteException.class)
    public void insertNull_fails() throws Exception
    {
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withMissingId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withNullId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, (Long) null);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);
    }

    @Test
    public void insertPutsInDb_andCanQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/collection/645");

        Cursor myCollection = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myCollection).isNotNull();
        //noinspection ConstantConditions
        assertThat(myCollection.moveToFirst()).isTrue();
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(myCollection.getLong(myCollection.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("James Bond Collection");
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
    }

    @Test
    public void insertTwiceTheSame_replaces() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/collection/645");

        values.put(CollectionContract.COLUMN_NAME, "Other collection");
        //noinspection ConstantConditions
        Uri replaced = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);
        //noinspection ConstantConditions
        assertThat(replaced.toString()).isEqualTo("content://content_authority/collection/645");

        Cursor myCollection = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myCollection).isNotNull();
        //noinspection ConstantConditions
        assertThat(myCollection.moveToFirst()).isTrue();
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(myCollection.getLong(myCollection.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Other collection");
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
    }

    @Test
    public void canInsertWithNullName() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract._ID, 645);

        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/collection/645");

        Cursor myCollection = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myCollection).isNotNull();
        //noinspection ConstantConditions
        assertThat(myCollection.moveToFirst()).isTrue();
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isNull();
        assertThat(myCollection.getLong(myCollection.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isNull();
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isNull();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void insertOnCollectionById_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                values);
    }

    @Test
    public void bulkInsert_with1Null_skips() throws Exception
    {
        ContentValues value2 = new ContentValues();
        value2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        value2.put(CollectionContract._ID, 12L);
        value2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        value2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        ContentValues[] values = new ContentValues[]{null, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values))
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withMissingId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        value1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        value1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        ContentValues value2 = new ContentValues();
        value2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        value2.put(CollectionContract._ID, 12L);
        value2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        value2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values))
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withNullId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        value1.put(CollectionContract._ID, (Long) null);
        value1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        value1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        ContentValues value2 = new ContentValues();
        value2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        value2.put(CollectionContract._ID, 12L);
        value2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        value2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values))
                .isEqualTo(1);
    }

    @Test
    public void bulkInsertInDb_andCanQuery() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        value1.put(CollectionContract._ID, 645L);
        value1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        value1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        ContentValues value2 = new ContentValues();
        value2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        value2.put(CollectionContract._ID, 12L);
        value2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        value2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        ContentValues[] values = new ContentValues[]{value1, value2};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values))
                .isEqualTo(2);

        Cursor myCollection = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection"),
                null, null, null, null, null, null, null);

        assertThat(myCollection).isNotNull();
        //noinspection ConstantConditions
        assertThat(myCollection.moveToFirst()).isTrue();
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/other_backdrop.jpg");
        assertThat(myCollection.getLong(myCollection.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(12L);
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Other Collection");
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/other_poster.jpg");

        assertThat(myCollection.moveToNext()).isTrue();
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(myCollection.getLong(myCollection.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("James Bond Collection");
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        assertThat(myCollection.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertEmptyInDb() throws Exception
    {
        ContentValues[] values = new ContentValues[0];
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values))
                .isEqualTo(0);

        Cursor myCollection = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection"),
                null, null, null, null, null, null, null);

        assertThat(myCollection).isNotNull();
        //noinspection ConstantConditions
        assertThat(myCollection.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertExisting_overwrites() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        value1.put(CollectionContract._ID, 645L);
        value1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        value1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        assertThat(providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                value1))
                .isEqualTo(Uri.parse("content://content_authority/collection/645"));

        ContentValues value2 = new ContentValues();
        value2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/yet_another_backdrop.jpg");
        value2.put(CollectionContract._ID, 645L);
        value2.put(CollectionContract.COLUMN_NAME, "Yet Another Collection");
        value2.put(CollectionContract.COLUMN_POSTER_PATH, "/yet_another_poster.jpg");
        ContentValues value3 = new ContentValues();
        value3.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        value3.put(CollectionContract._ID, 12L);
        value3.put(CollectionContract.COLUMN_NAME, "Other Collection");
        value3.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        ContentValues[] values = new ContentValues[]{value1, value2, value3};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values))
                .isEqualTo(3);

        Cursor myCollection = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection"),
                null, null, null, null, null, null, null);

        assertThat(myCollection).isNotNull();
        //noinspection ConstantConditions
        assertThat(myCollection.moveToFirst()).isTrue();
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/other_backdrop.jpg");
        assertThat(myCollection.getLong(myCollection.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(12L);
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Other Collection");
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/other_poster.jpg");

        assertThat(myCollection.moveToNext()).isTrue();
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/yet_another_backdrop.jpg");
        assertThat(myCollection.getLong(myCollection.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Yet Another Collection");
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/yet_another_poster.jpg");

        assertThat(myCollection.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertDuplicate_lastWins() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        value1.put(CollectionContract._ID, 645L);
        value1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        value1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        ContentValues value2 = new ContentValues();
        value2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/yet_another_backdrop.jpg");
        value2.put(CollectionContract._ID, 645L);
        value2.put(CollectionContract.COLUMN_NAME, "Yet Another Collection");
        value2.put(CollectionContract.COLUMN_POSTER_PATH, "/yet_another_poster.jpg");
        ContentValues value3 = new ContentValues();
        value3.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        value3.put(CollectionContract._ID, 12L);
        value3.put(CollectionContract.COLUMN_NAME, "Other Collection");
        value3.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        ContentValues[] values = new ContentValues[]{value1, value2, value3};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values))
                .isEqualTo(3);

        Cursor myCollection = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection"),
                null, null, null, null, null, null, null);

        assertThat(myCollection).isNotNull();
        //noinspection ConstantConditions
        assertThat(myCollection.moveToFirst()).isTrue();
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/other_backdrop.jpg");
        assertThat(myCollection.getLong(myCollection.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(12L);
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Other Collection");
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/other_poster.jpg");

        assertThat(myCollection.moveToNext()).isTrue();
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/yet_another_backdrop.jpg");
        assertThat(myCollection.getLong(myCollection.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Yet Another Collection");
        assertThat(myCollection.getString(myCollection.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/yet_another_poster.jpg");

        assertThat(myCollection.moveToNext()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void bulkInsertOnCollectionById_fails() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        value1.put(CollectionContract._ID, 645L);
        value1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        value1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        ContentValues value2 = new ContentValues();
        value2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        value2.put(CollectionContract._ID, 12L);
        value2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        value2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                new ContentValues[]{value1, value2});
    }

    @Test
    public void queryListFromDb_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        value1.put(CollectionContract._ID, 645L);
        value1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        value1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                value1);
        ContentValues value2 = new ContentValues();
        value2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        value2.put(CollectionContract._ID, 12L);
        value2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        value2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                value2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection"),
                null, null, null, null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/other_backdrop.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(12L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Other Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/other_poster.jpg");

        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("James Bond Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryListFromDbWithSelection_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        value1.put(CollectionContract._ID, 645L);
        value1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        value1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                value1);
        ContentValues value2 = new ContentValues();
        value2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        value2.put(CollectionContract._ID, 12L);
        value2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        value2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                value2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection"),
                null,
                CollectionContract.COLUMN_NAME + "=?",
                new String[]{"Other Collection"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/other_backdrop.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(12L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Other Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/other_poster.jpg");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                null,
                CollectionContract.COLUMN_NAME + "=?",
                new String[]{"James Bond Collection"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("James Bond Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithContradictorySelection_returnsEmpty() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                null,
                CollectionContract.COLUMN_NAME + "=?",
                new String[]{"Other Collection"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);
    }

    @Test
    public void deleteItem_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                null, null)).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection"),
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
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                CollectionContract.COLUMN_NAME + "=?",
                new String[]{"James Bond Collection"})).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection"),
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
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                CollectionContract.COLUMN_NAME + "=?",
                new String[]{"Other Collection"})).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
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
        values1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values1.put(CollectionContract._ID, 645L);
        values1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        values2.put(CollectionContract._ID, 12L);
        values2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        values2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values2);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                CollectionContract.COLUMN_NAME + "=?",
                new String[]{"Other Collection"})).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection"),
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
        values1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values1.put(CollectionContract._ID, 645L);
        values1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        values2.put(CollectionContract._ID, 12L);
        values2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        values2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values2);
        ContentValues values3 = new ContentValues();
        values3.put(CollectionContract.COLUMN_BACKDROP_PATH, "/another_backdrop.jpg");
        values3.put(CollectionContract._ID, 20L);
        values3.put(CollectionContract.COLUMN_NAME, "James Bond Other Collection");
        values3.put(CollectionContract.COLUMN_POSTER_PATH, "/another_poster.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values3);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                CollectionContract.COLUMN_NAME + " LIKE ?",
                new String[]{"James%"})).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection"),
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
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(CollectionContract.COLUMN_NAME, "Other Collection");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                newValues,
                null, null)).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Other Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
    }

    @Test
    public void updateItemWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(CollectionContract.COLUMN_NAME, "Other Collection");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                newValues,
                CollectionContract.COLUMN_NAME + "=?",
                new String[]{"James Bond Collection"})).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Other Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
    }

    @Test
    public void updateItemWithChangedId_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(CollectionContract._ID, 4L);
        newValues.put(CollectionContract.COLUMN_NAME, "Other Collection");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                newValues,
                CollectionContract.COLUMN_NAME + "=?",
                new String[]{"James Bond Collection"})).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/4"),
                null,
                null,
                null,
                null, null, null, null);
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(4L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Other Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
    }

    @Test
    public void updateItemWithContradictorySelection_doesNotUpdate() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(CollectionContract.COLUMN_NAME, "Other Collection");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                newValues,
                CollectionContract.COLUMN_NAME + "=?",
                new String[]{"Fake Collection"})).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("James Bond Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
    }

    @Test
    public void updateItemWithMissingElement_doesNotLoseSavedInfo() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(CollectionContract._ID, 645L);
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                newValues,
                null, null)).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("James Bond Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
    }

    @Test
    public void updateList_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);
        ContentValues values2 = new ContentValues();
        values2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        values2.put(CollectionContract._ID, 12L);
        values2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        values2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values2);

        ContentValues newValues = new ContentValues();
        newValues.put(CollectionContract.COLUMN_NAME, "Common Name Collection");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                newValues,
                null,
                null)).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Common Name Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/12"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/other_backdrop.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(12L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Common Name Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/other_poster.jpg");
    }

    @Test
    public void updateListWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values);
        ContentValues values2 = new ContentValues();
        values2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        values2.put(CollectionContract._ID, 12L);
        values2.put(CollectionContract.COLUMN_NAME, "James Bond Other Collection");
        values2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values2);
        ContentValues values3 = new ContentValues();
        values3.put(CollectionContract.COLUMN_BACKDROP_PATH, "/another_backdrop.jpg");
        values3.put(CollectionContract._ID, 5L);
        values3.put(CollectionContract.COLUMN_NAME, "Yet Another Collection");
        values3.put(CollectionContract.COLUMN_POSTER_PATH, "/another_poster.jpg");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                values3);

        ContentValues newValues = new ContentValues();
        newValues.put(CollectionContract.COLUMN_NAME, "Common Name Collection");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection"),
                newValues,
                CollectionContract.TABLE_NAME + "." + CollectionContract._ID + "<=?",
                new String[]{"12"})).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/12"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/other_backdrop.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(12L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Common Name Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/other_poster.jpg");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/5"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/another_backdrop.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(5L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("Common Name Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/another_poster.jpg");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/645"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_BACKDROP_PATH)))
                .isEqualTo("/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        assertThat(found.getLong(found.getColumnIndex(CollectionContract._ID)))
                .isEqualTo(645L);
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_NAME)))
                .isEqualTo("James Bond Collection");
        assertThat(found.getString(found.getColumnIndex(CollectionContract.COLUMN_POSTER_PATH)))
                .isEqualTo("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
    }
}