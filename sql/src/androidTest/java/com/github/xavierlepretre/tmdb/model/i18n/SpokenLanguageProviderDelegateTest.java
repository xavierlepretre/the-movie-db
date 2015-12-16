package com.github.xavierlepretre.tmdb.model.i18n;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class SpokenLanguageProviderDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.spokenLanguage.db";
    private SpokenLanguageProviderDelegate providerDelegate;
    private EntitySQLiteOpenHelper sqlHelper;

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = spy(new SpokenLanguageProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/spokenLanguage"),
                "dir_type",
                "item_type"));
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new SpokenLanguageSQLHelperDelegate());
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
        assertThat(matcher.match(Uri.parse("content://content_authority/spokenLanguage"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/spokenLanguage/a"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/spokenLanguage/456"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/spokenLanguage/456/t"))).isEqualTo(UriMatcher.NO_MATCH);
    }

    @Test
    public void returnsProperTypes() throws Exception
    {
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/spokenLanguage"))).isEqualTo("dir_type");
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/spokenLanguage/en"))).isEqualTo("item_type");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_mayFail() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/fake"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void properTypesFailsOnUnknownPath() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/spokenLanguage/FAKE"));
    }

    @Test
    public void getSpokenLanguageFromPath_isOk() throws Exception
    {
        assertThat(providerDelegate.getSpokenLanguageId(Uri.parse("content://content_authority/spokenLanguage/1"))).isEqualTo("1");
        assertThat(providerDelegate.getSpokenLanguageId(Uri.parse("content://content_authority/spokenLanguage/a"))).isEqualTo("a");
        assertThat(providerDelegate.getSpokenLanguageId(Uri.parse("content://anything/spokenLanguage/a"))).isEqualTo("a");
        assertThat(providerDelegate.getSpokenLanguageId(Uri.parse("content://anything/fake/a"))).isEqualTo("a");
        assertThat(providerDelegate.getSpokenLanguageId(Uri.parse("content://anything/fake/2"))).isEqualTo("2");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getSpokenLanguageFromPath_canFail() throws Exception
    {
        providerDelegate.getSpokenLanguageId(Uri.parse("content://content_authority/spokenLanguage"));
    }

    @Test
    public void buildSpokenLanguageLocation_isOk() throws Exception
    {
        assertThat(SpokenLanguageProviderDelegate.buildSpokenLanguageLocation(
                Uri.parse("content://something"), LanguageCode.en))
                .isEqualTo(Uri.parse("content://something/en"));
        assertThat(providerDelegate.buildSpokenLanguageLocation(LanguageCode.fr))
                .isEqualTo(Uri.parse("content://content_authority/spokenLanguage/fr"));
    }

    @Test(expected = SQLiteException.class)
    public void insertNull_fails() throws Exception
    {
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withMissingId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withNullId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, (String) null);
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);
    }

    @Test
    public void insertPutsInDb_andCanQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/spokenLanguage/en");

        Cursor mySpokenLanguage = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(mySpokenLanguage).isNotNull();
        //noinspection ConstantConditions
        assertThat(mySpokenLanguage.moveToFirst()).isTrue();
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("en");
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isEqualTo("English");
    }

    @Test
    public void insertTwiceTheSame_replaces() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/spokenLanguage/en");

        values.put(SpokenLanguageContract.COLUMN_NAME, "Anglais");
        //noinspection ConstantConditions
        Uri replaced = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(replaced.toString()).isEqualTo("content://content_authority/spokenLanguage/en");

        Cursor mySpokenLanguage = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(mySpokenLanguage).isNotNull();
        //noinspection ConstantConditions
        assertThat(mySpokenLanguage.moveToFirst()).isTrue();
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("en");
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isEqualTo("Anglais");
    }

    @Test
    public void canInsertWithNullName() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");

        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/spokenLanguage/en");

        Cursor mySpokenLanguage = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(mySpokenLanguage).isNotNull();
        //noinspection ConstantConditions
        assertThat(mySpokenLanguage.moveToFirst()).isTrue();
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("en");
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isNull();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void insertOnSpokenLanguageById_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                values);
    }

    @Test
    public void bulkInsert_with1Null_skips() throws Exception
    {
        ContentValues value2 = new ContentValues();
        value2.put(SpokenLanguageContract._ID, "en");
        value2.put(SpokenLanguageContract.COLUMN_NAME, "English");
        ContentValues[] values = new ContentValues[]{null, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withMissingId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        ContentValues value2 = new ContentValues();
        value2.put(SpokenLanguageContract._ID, "fr");
        value2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withNullId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(SpokenLanguageContract._ID, (String) null);
        value1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        ContentValues value2 = new ContentValues();
        value2.put(SpokenLanguageContract._ID, "fr");
        value2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsertInDb_andCanQuery() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(SpokenLanguageContract._ID, "en");
        value1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        ContentValues value2 = new ContentValues();
        value2.put(SpokenLanguageContract._ID, "fr");
        value2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        ContentValues[] values = new ContentValues[]{value1, value2};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values).getCount())
                .isEqualTo(2);

        Cursor mySpokenLanguage = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                null, null, null, null, null, null, null);

        assertThat(mySpokenLanguage).isNotNull();
        //noinspection ConstantConditions
        assertThat(mySpokenLanguage.moveToFirst()).isTrue();
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("en");
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isEqualTo("English");
        assertThat(mySpokenLanguage.moveToNext()).isTrue();
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("fr");
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isEqualTo("French");
        assertThat(mySpokenLanguage.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertEmptyInDb() throws Exception
    {
        ContentValues[] values = new ContentValues[0];
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values).getCount())
                .isEqualTo(0);

        Cursor mySpokenLanguage = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                null, null, null, null, null, null, null);

        assertThat(mySpokenLanguage).isNotNull();
        //noinspection ConstantConditions
        assertThat(mySpokenLanguage.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertExisting_overwrites() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(SpokenLanguageContract._ID, "en");
        value1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        assertThat(providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                value1).getInserted())
                .isEqualTo(Uri.parse("content://content_authority/spokenLanguage/en"));

        ContentValues value2 = new ContentValues();
        value2.put(SpokenLanguageContract._ID, "en");
        value2.put(SpokenLanguageContract.COLUMN_NAME, "Anglais");
        ContentValues value3 = new ContentValues();
        value3.put(SpokenLanguageContract._ID, "fr");
        value3.put(SpokenLanguageContract.COLUMN_NAME, "French");
        ContentValues[] values = new ContentValues[]{value1, value3, value2};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values).getCount())
                .isEqualTo(3);

        Cursor mySpokenLanguage = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                null, null, null, null, null, SpokenLanguageContract._ID, null);

        assertThat(mySpokenLanguage).isNotNull();
        //noinspection ConstantConditions
        assertThat(mySpokenLanguage.moveToFirst()).isTrue();
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("en");
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isEqualTo("Anglais");
        assertThat(mySpokenLanguage.moveToNext()).isTrue();
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("fr");
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isEqualTo("French");
        assertThat(mySpokenLanguage.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertDuplicate_lastWins() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(SpokenLanguageContract._ID, "en");
        value1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        ContentValues value2 = new ContentValues();
        value2.put(SpokenLanguageContract._ID, "en");
        value2.put(SpokenLanguageContract.COLUMN_NAME, "Anglais");
        ContentValues value3 = new ContentValues();
        value3.put(SpokenLanguageContract._ID, "fr");
        value3.put(SpokenLanguageContract.COLUMN_NAME, "French");
        ContentValues[] values = new ContentValues[]{value1, value2, value3};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values).getCount())
                .isEqualTo(3);

        Cursor mySpokenLanguage = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                null, null, null, null, null, null, null);

        assertThat(mySpokenLanguage).isNotNull();
        //noinspection ConstantConditions
        assertThat(mySpokenLanguage.moveToFirst()).isTrue();
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("en");
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isEqualTo("Anglais");
        assertThat(mySpokenLanguage.moveToNext()).isTrue();
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("fr");
        assertThat(mySpokenLanguage.getString(mySpokenLanguage.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isEqualTo("French");
        assertThat(mySpokenLanguage.moveToNext()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void bulkInsertOnSpokenLanguageById_fails() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(SpokenLanguageContract._ID, "en");
        value1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        ContentValues value2 = new ContentValues();
        value2.put(SpokenLanguageContract._ID, "fr");
        value2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                new ContentValues[]{value1, value2});
    }

    @Test
    public void queryListFromDb_isOk() throws Exception
    {
        ContentValues values1 = new ContentValues();
        values1.put(SpokenLanguageContract._ID, "en");
        values1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(SpokenLanguageContract._ID, "fr");
        values2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                null, null, null, null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("en");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isEqualTo("English");
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("fr");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isEqualTo("French");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryListFromDbWithSelection_isOk() throws Exception
    {
        ContentValues values1 = new ContentValues();
        values1.put(SpokenLanguageContract._ID, "en");
        values1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(SpokenLanguageContract._ID, "fr");
        values2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                null,
                SpokenLanguageContract.COLUMN_NAME + "=?",
                new String[]{"French"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("fr");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isEqualTo("French");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                null,
                SpokenLanguageContract.COLUMN_NAME + "=?",
                new String[]{"English"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID)))
                .isEqualTo("en");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME)))
                .isEqualTo("English");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithContradictorySelection_returnsEmpty() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                null,
                SpokenLanguageContract.COLUMN_NAME + "=?",
                new String[]{"Fench"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);
    }

    @Test
    public void deleteItem_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                null, null).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
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
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                SpokenLanguageContract.COLUMN_NAME + "=?",
                new String[]{"English"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
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
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                SpokenLanguageContract.COLUMN_NAME + "=?",
                new String[]{"French"}).getCount()).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
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
        values1.put(SpokenLanguageContract._ID, "en");
        values1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(SpokenLanguageContract._ID, "fr");
        values2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values2);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                SpokenLanguageContract.COLUMN_NAME + "=?",
                new String[]{"French"}).getCount()).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
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
        values1.put(SpokenLanguageContract._ID, "en");
        values1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(SpokenLanguageContract._ID, "fr");
        values2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values2);
        ContentValues values3 = new ContentValues();
        values3.put(SpokenLanguageContract._ID, "et");
        values3.put(SpokenLanguageContract.COLUMN_NAME, "Estonian");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values3);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                SpokenLanguageContract.COLUMN_NAME + " LIKE ?",
                new String[]{"E%"}).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
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
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(SpokenLanguageContract.COLUMN_NAME, "Anglais");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                newValues,
                null, null).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID))).isEqualTo("en");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME))).isEqualTo("Anglais");
    }

    @Test
    public void updateItemWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(SpokenLanguageContract.COLUMN_NAME, "Anglais");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                newValues,
                SpokenLanguageContract.COLUMN_NAME + "=?",
                new String[]{"English"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID))).isEqualTo("en");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME))).isEqualTo("Anglais");
    }

    @Test
    public void updateItemWithChangedId_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(SpokenLanguageContract._ID, "fr");
        newValues.put(SpokenLanguageContract.COLUMN_NAME, "French");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                newValues,
                SpokenLanguageContract.COLUMN_NAME + "=?",
                new String[]{"English"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/fr"),
                null,
                null,
                null,
                null, null, null, null);
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID))).isEqualTo("fr");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME))).isEqualTo("French");
    }

    @Test
    public void updateItemWithContradictorySelection_doesNotUpdate() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(SpokenLanguageContract.COLUMN_NAME, "Anglais");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                newValues,
                SpokenLanguageContract.COLUMN_NAME + "=?",
                new String[]{"French"}).getCount()).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID))).isEqualTo("en");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME))).isEqualTo("English");
    }

    @Test
    public void updateItemWithMissingElement_doesNotLoseSavedInfo() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(SpokenLanguageContract._ID, "en");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                newValues,
                null, null).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID))).isEqualTo("en");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME))).isEqualTo("English");
    }

    @Test
    public void updateList_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);
        ContentValues values2 = new ContentValues();
        values2.put(SpokenLanguageContract._ID, "fr");
        values2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values2);

        ContentValues newValues = new ContentValues();
        newValues.put(SpokenLanguageContract.COLUMN_NAME, "Language");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                newValues,
                null,
                null).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID))).isEqualTo("en");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME))).isEqualTo("Language");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/fr"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID))).isEqualTo("fr");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME))).isEqualTo("Language");
    }

    @Test
    public void updateListWithSelection_isOk() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values);
        ContentValues values2 = new ContentValues();
        values2.put(SpokenLanguageContract._ID, "fr");
        values2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values2);
        ContentValues values3 = new ContentValues();
        values3.put(SpokenLanguageContract._ID, "et");
        values3.put(SpokenLanguageContract.COLUMN_NAME, "Estonian");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                values3);

        ContentValues newValues = new ContentValues();
        newValues.put(SpokenLanguageContract.COLUMN_NAME, "Language");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage"),
                newValues,
                SpokenLanguageContract.TABLE_NAME + "." + SpokenLanguageContract.COLUMN_NAME + " LIKE ?",
                new String[]{"E%"}).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/en"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID))).isEqualTo("en");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME))).isEqualTo("Language");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/fr"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID))).isEqualTo("fr");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME))).isEqualTo("French");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/spokenLanguage/et"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract._ID))).isEqualTo("et");
        assertThat(found.getString(found.getColumnIndex(SpokenLanguageContract.COLUMN_NAME))).isEqualTo("Language");
    }
}