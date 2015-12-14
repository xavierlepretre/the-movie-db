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

public class GenreProviderDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.genre.db";
    private GenreProviderDelegate providerDelegate;
    private EntitySQLiteOpenHelper sqlHelper;

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = spy(new GenreProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/genre"),
                "dir_type",
                "item_type"));
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new GenreSQLHelperDelegate());
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
        assertThat(matcher.match(Uri.parse("content://content_authority/genre"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/genre/a"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/genre/456"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/genre/456/t"))).isEqualTo(UriMatcher.NO_MATCH);
    }

    @Test
    public void returnsProperTypes() throws Exception
    {
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/genre"))).isEqualTo("dir_type");
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/genre/34"))).isEqualTo("item_type");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_mayFail() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/fake"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void properTypesFailsOnUnknownPath() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/genre/a"));
    }

    @Test
    public void getGenreFromPath_isOk() throws Exception
    {
        assertThat(providerDelegate.getGenreId(Uri.parse("content://content_authority/genre/1"))).isEqualTo("1");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://content_authority/genre/a"))).isEqualTo("a");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://anything/genre/a"))).isEqualTo("a");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://anything/fake/a"))).isEqualTo("a");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://anything/fake/2"))).isEqualTo("2");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getGenreFromPath_canFail() throws Exception
    {
        providerDelegate.getGenreId(Uri.parse("content://content_authority/genre"));
    }

    @Test
    public void buildGenreLocation_isOk() throws Exception
    {
        assertThat(GenreProviderDelegate.buildGenreLocation(
                Uri.parse("content://something"), new GenreId(60)))
                .isEqualTo(Uri.parse("content://something/60"));
        assertThat(providerDelegate.buildGenreLocation(new GenreId(870)))
                .isEqualTo(Uri.parse("content://content_authority/genre/870"));
    }

    @Test(expected = SQLiteException.class)
    public void insertNull_fails() throws Exception
    {
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withMissingId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withNullId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, (Integer) null);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);
    }

    @Test
    public void insertPutsInDb_andCanQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/genre/3");

        Cursor myGenre = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myGenre).isNotNull();
        //noinspection ConstantConditions
        assertThat(myGenre.moveToFirst()).isTrue();
        assertThat(myGenre.getString(myGenre.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isEqualTo("Adventure");
    }

    @Test
    public void insertTwiceTheSame_replaces() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/genre/3");

        values.put(GenreContract.COLUMN_NAME, "Comic");
        //noinspection ConstantConditions
        Uri replaced = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);
        //noinspection ConstantConditions
        assertThat(replaced.toString()).isEqualTo("content://content_authority/genre/3");

        Cursor myGenre = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myGenre).isNotNull();
        //noinspection ConstantConditions
        assertThat(myGenre.moveToFirst()).isTrue();
        assertThat(myGenre.getString(myGenre.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isEqualTo("Comic");
    }

    @Test
    public void canInsertWithNullName() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);

        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/genre/3");

        Cursor myGenre = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myGenre).isNotNull();
        //noinspection ConstantConditions
        assertThat(myGenre.moveToFirst()).isTrue();
        assertThat(myGenre.getString(myGenre.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isNull();
    }

    @Test
    public void bulkInsert_with1Null_skips() throws Exception
    {
        ContentValues value2 = new ContentValues();
        value2.put(GenreContract._ID, 4);
        value2.put(GenreContract.COLUMN_NAME, "Comic");
        ContentValues[] values = new ContentValues[]{null, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values))
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withMissingId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(GenreContract.COLUMN_NAME, "Adventure");
        ContentValues value2 = new ContentValues();
        value2.put(GenreContract._ID, 4);
        value2.put(GenreContract.COLUMN_NAME, "Comic");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values))
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withNullId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(GenreContract._ID, (Integer) null);
        value1.put(GenreContract.COLUMN_NAME, "Adventure");
        ContentValues value2 = new ContentValues();
        value2.put(GenreContract._ID, 4);
        value2.put(GenreContract.COLUMN_NAME, "Comic");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values))
                .isEqualTo(1);
    }

    @Test
    public void bulkInsertInDb_andCanQuery() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(GenreContract._ID, 3);
        value1.put(GenreContract.COLUMN_NAME, "Adventure");
        ContentValues value2 = new ContentValues();
        value2.put(GenreContract._ID, 4);
        value2.put(GenreContract.COLUMN_NAME, "Comic");
        ContentValues[] values = new ContentValues[]{value1, value2};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values))
                .isEqualTo(2);

        Cursor myGenre = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre"),
                null, null, null, null, null, null, null);

        assertThat(myGenre).isNotNull();
        //noinspection ConstantConditions
        assertThat(myGenre.moveToFirst()).isTrue();
        assertThat(myGenre.getString(myGenre.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isEqualTo("Adventure");
        assertThat(myGenre.moveToNext()).isTrue();
        assertThat(myGenre.getString(myGenre.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isEqualTo("Comic");
        assertThat(myGenre.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertEmptyInDb() throws Exception
    {
        ContentValues[] values = new ContentValues[0];
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values))
                .isEqualTo(0);

        Cursor myGenre = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre"),
                null, null, null, null, null, null, null);

        assertThat(myGenre).isNotNull();
        //noinspection ConstantConditions
        assertThat(myGenre.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertExisting_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(GenreContract._ID, 3);
        value1.put(GenreContract.COLUMN_NAME, "Adventure");
        assertThat(providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                value1))
                .isEqualTo(Uri.parse("content://content_authority/genre/3"));

        ContentValues value2 = new ContentValues();
        value2.put(GenreContract._ID, 4);
        value2.put(GenreContract.COLUMN_NAME, "Comic");
        ContentValues[] values = new ContentValues[]{value1, value2};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values))
                .isEqualTo(1);

        Cursor myGenre = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre"),
                null, null, null, null, null, null, null);

        assertThat(myGenre).isNotNull();
        //noinspection ConstantConditions
        assertThat(myGenre.moveToFirst()).isTrue();
        assertThat(myGenre.getString(myGenre.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isEqualTo("Adventure");
        assertThat(myGenre.moveToNext()).isTrue();
        assertThat(myGenre.getString(myGenre.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isEqualTo("Comic");
        assertThat(myGenre.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertDuplicate_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(GenreContract._ID, 3);
        value1.put(GenreContract.COLUMN_NAME, "Adventure");
        ContentValues value2 = new ContentValues();
        value2.put(GenreContract._ID, 3);
        value2.put(GenreContract.COLUMN_NAME, "Adventure");
        ContentValues value3 = new ContentValues();
        value3.put(GenreContract._ID, 4);
        value3.put(GenreContract.COLUMN_NAME, "Comic");
        ContentValues[] values = new ContentValues[]{value1, value2, value3};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values))
                .isEqualTo(2);

        Cursor myGenre = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre"),
                null, null, null, null, null, null, null);

        assertThat(myGenre).isNotNull();
        //noinspection ConstantConditions
        assertThat(myGenre.moveToFirst()).isTrue();
        assertThat(myGenre.getString(myGenre.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isEqualTo("Adventure");
        assertThat(myGenre.moveToNext()).isTrue();
        assertThat(myGenre.getString(myGenre.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isEqualTo("Comic");
        assertThat(myGenre.moveToNext()).isFalse();
    }

    @Test
    public void queryListFromDb_isOk() throws Exception
    {
        ContentValues values1 = new ContentValues();
        values1.put(GenreContract._ID, 3);
        values1.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(GenreContract._ID, 5);
        values2.put(GenreContract.COLUMN_NAME, "Comic");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre"),
                null, null, null, null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID)))
                .isEqualTo(3);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isEqualTo("Adventure");
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID)))
                .isEqualTo(5);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isEqualTo("Comic");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryListFromDbWithSelection_isOk() throws Exception
    {
        ContentValues values1 = new ContentValues();
        values1.put(GenreContract._ID, 3);
        values1.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(GenreContract._ID, 5);
        values2.put(GenreContract.COLUMN_NAME, "Comic");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre"),
                null,
                GenreContract.COLUMN_NAME + "=?",
                new String[]{"Comic"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID)))
                .isEqualTo(5);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isEqualTo("Comic");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                null,
                GenreContract.COLUMN_NAME + "=?",
                new String[]{"Adventure"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID)))
                .isEqualTo(3);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME)))
                .isEqualTo("Adventure");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithContradictorySelection_returnsEmpty() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                null,
                GenreContract.COLUMN_NAME + "=?",
                new String[]{"Comic"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);
    }

    @Test
    public void deleteItem_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                null, null)).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre"),
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
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                GenreContract.COLUMN_NAME + "=?",
                new String[]{"Adventure"})).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre"),
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
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                GenreContract.COLUMN_NAME + "=?",
                new String[]{"Comic"})).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
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
        values1.put(GenreContract._ID, 3);
        values1.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(GenreContract._ID, 4);
        values2.put(GenreContract.COLUMN_NAME, "Comic");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values2);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                GenreContract.COLUMN_NAME + "=?",
                new String[]{"Comic"})).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre"),
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
        values1.put(GenreContract._ID, 3);
        values1.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(GenreContract._ID, 4);
        values2.put(GenreContract.COLUMN_NAME, "Action");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values2);
        ContentValues values3 = new ContentValues();
        values3.put(GenreContract._ID, 5);
        values3.put(GenreContract.COLUMN_NAME, "Comic");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values3);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                GenreContract.COLUMN_NAME + " LIKE ?",
                new String[]{"A%"})).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre"),
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
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(GenreContract.COLUMN_NAME, "Comic");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                newValues,
                null, null)).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID))).isEqualTo(3);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME))).isEqualTo("Comic");
    }

    @Test
    public void updateItemWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(GenreContract.COLUMN_NAME, "Comic");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                newValues,
                GenreContract.COLUMN_NAME + "=?",
                new String[]{"Adventure"})).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID))).isEqualTo(3);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME))).isEqualTo("Comic");
    }

    @Test
    public void updateItemWithChangedId_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(GenreContract._ID, 4);
        newValues.put(GenreContract.COLUMN_NAME, "Comic");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                newValues,
                GenreContract.COLUMN_NAME + "=?",
                new String[]{"Adventure"})).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/4"),
                null,
                null,
                null,
                null, null, null, null);
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID))).isEqualTo(4);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME))).isEqualTo("Comic");
    }

    @Test
    public void updateItemWithContradictorySelection_doesNotUpdate() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(GenreContract.COLUMN_NAME, "Comic");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                newValues,
                GenreContract.COLUMN_NAME + "=?",
                new String[]{"Comic"})).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID))).isEqualTo(3);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME))).isEqualTo("Adventure");
    }

    @Test
    public void updateList_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);
        ContentValues values2 = new ContentValues();
        values2.put(GenreContract._ID, 4);
        values2.put(GenreContract.COLUMN_NAME, "Action");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values2);

        ContentValues newValues = new ContentValues();
        newValues.put(GenreContract.COLUMN_NAME, "Comic");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                newValues,
                null,
                null)).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID))).isEqualTo(3);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME))).isEqualTo("Comic");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/4"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID))).isEqualTo(4);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME))).isEqualTo("Comic");
    }

    @Test
    public void updateListWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values);
        ContentValues values2 = new ContentValues();
        values2.put(GenreContract._ID, 4);
        values2.put(GenreContract.COLUMN_NAME, "Action");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values2);
        ContentValues values3 = new ContentValues();
        values3.put(GenreContract._ID, 5);
        values3.put(GenreContract.COLUMN_NAME, "Artistic");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                values3);

        ContentValues newValues = new ContentValues();
        newValues.put(GenreContract.COLUMN_NAME, "Comic");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                newValues,
                GenreContract.TABLE_NAME + "." + GenreContract._ID + "<=?",
                new String[]{"4"})).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/3"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID))).isEqualTo(3);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME))).isEqualTo("Comic");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/4"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID))).isEqualTo(4);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME))).isEqualTo("Comic");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/5"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(GenreContract._ID))).isEqualTo(5);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME))).isEqualTo("Artistic");
    }
}