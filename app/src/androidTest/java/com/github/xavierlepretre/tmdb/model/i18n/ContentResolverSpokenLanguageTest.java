package com.github.xavierlepretre.tmdb.model.i18n;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.TmdbContract.SpokenLanguageEntity;
import com.github.xavierlepretre.tmdb.sql.i18n.SpokenLanguageCursor;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class ContentResolverSpokenLanguageTest
{
    private Cursor cursor;
    private List<ContentObserver> observers;

    @Before
    public void setUp() throws Exception
    {
        deleteFromTable();
        observers = new ArrayList<>();
    }

    @After
    public void tearDown() throws Exception
    {
        if (cursor != null)
        {
            try
            {
                cursor.close();
            }
            catch (Exception ignored)
            {
                // Nothing to do
            }
        }
        deleteFromTable();
        for (ContentObserver observer : observers)
        {
            InstrumentationRegistry.getContext().getContentResolver().unregisterContentObserver(observer);
        }
        observers.clear();
    }

    private void deleteFromTable()
    {
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(SpokenLanguageEntity.CONTENT_URI, null, null);
    }

    @Test
    public void canGetTypes() throws Exception
    {
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                SpokenLanguageEntity.CONTENT_URI))
                .isEqualTo(SpokenLanguageEntity.CONTENT_DIR_TYPE);
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                SpokenLanguageEntity.buildUri(LanguageCode.vi)))
                .isEqualTo(SpokenLanguageEntity.CONTENT_ITEM_TYPE);
    }

    @Test
    public void canInsertQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                SpokenLanguageEntity.buildUri(LanguageCode.en).toString());

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        SpokenLanguageCursor spokenLanguageCursor = new SpokenLanguageCursor(cursor);
        assertThat(spokenLanguageCursor.moveToFirst()).isTrue();
        SpokenLanguage spokenLanguage = spokenLanguageCursor.getSpokenLanguage();
        assertThat(spokenLanguage.getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(spokenLanguage.getName()).isEqualTo("English");
        assertThat(spokenLanguageCursor.moveToNext()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void insertOnSpokenLanguageById_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                SpokenLanguageEntity.buildUri(LanguageCode.en),
                values);
    }

    @Test
    public void insert_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                SpokenLanguageEntity.CONTENT_URI,
                true,
                observer);

        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(inserted);
    }

    @Test
    public void insert_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                SpokenLanguageEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(0);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                SpokenLanguageEntity.CONTENT_URI);

        values.put(SpokenLanguageContract._ID, "fr");
        values.put(SpokenLanguageContract.COLUMN_NAME, "French");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canInsertBulk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(SpokenLanguageContract._ID, "en");
        value1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        ContentValues value2 = new ContentValues();
        value2.put(SpokenLanguageContract._ID, "fr");
        value2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                SpokenLanguageEntity.CONTENT_URI,
                values))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                SpokenLanguageEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        SpokenLanguageCursor spokenLanguageCursor = new SpokenLanguageCursor(cursor);
        assertThat(spokenLanguageCursor.moveToFirst()).isTrue();
        SpokenLanguage spokenLanguage = spokenLanguageCursor.getSpokenLanguage();
        assertThat(spokenLanguage.getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(spokenLanguage.getName()).isEqualTo("English");
        assertThat(spokenLanguageCursor.moveToNext()).isTrue();
        spokenLanguage = spokenLanguageCursor.getSpokenLanguage();
        assertThat(spokenLanguage.getIso639Dash1()).isEqualTo(LanguageCode.fr);
        assertThat(spokenLanguage.getName()).isEqualTo("French");
        assertThat(spokenLanguageCursor.moveToNext()).isFalse();
    }

    @Test
    public void canInsertBulkWithEmptyArray() throws Exception
    {
        ContentValues[] values = new ContentValues[0];

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                SpokenLanguageEntity.CONTENT_URI,
                values))
                .isEqualTo(0);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                SpokenLanguageEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void insertBulkTheSame_lastWins() throws Exception
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

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                SpokenLanguageEntity.CONTENT_URI,
                values))
                .isEqualTo(3);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                SpokenLanguageEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        SpokenLanguageCursor spokenLanguageCursor = new SpokenLanguageCursor(cursor);
        assertThat(spokenLanguageCursor.moveToFirst()).isTrue();
        SpokenLanguage spokenLanguage = spokenLanguageCursor.getSpokenLanguage();
        assertThat(spokenLanguage.getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(spokenLanguage.getName()).isEqualTo("Anglais");
        assertThat(spokenLanguageCursor.moveToNext()).isTrue();
        spokenLanguage = spokenLanguageCursor.getSpokenLanguage();
        assertThat(spokenLanguage.getIso639Dash1()).isEqualTo(LanguageCode.fr);
        assertThat(spokenLanguage.getName()).isEqualTo("French");
        assertThat(spokenLanguageCursor.moveToNext()).isFalse();
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
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                SpokenLanguageEntity.buildUri(LanguageCode.en),
                new ContentValues[]{value1, value2});
    }

    @Test
    public void insertBulk_getsNotified() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(SpokenLanguageContract._ID, "en");
        value1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        ContentValues value2 = new ContentValues();
        value2.put(SpokenLanguageContract._ID, "fr");
        value2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        ContentValues[] values = new ContentValues[]{value1, value2};

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                SpokenLanguageEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(SpokenLanguageEntity.CONTENT_URI);
    }

    @Test
    public void insertBulk_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                SpokenLanguageEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(0);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        ContentValues value1 = new ContentValues();
        value1.put(SpokenLanguageContract._ID, "en");
        value1.put(SpokenLanguageContract.COLUMN_NAME, "English");
        ContentValues value2 = new ContentValues();
        value2.put(SpokenLanguageContract._ID, "fr");
        value2.put(SpokenLanguageContract.COLUMN_NAME, "French");
        ContentValues[] values = new ContentValues[]{value1, value2};
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                SpokenLanguageEntity.CONTENT_URI);

        value1.put(SpokenLanguageContract._ID, "de");
        value1.put(SpokenLanguageContract.COLUMN_NAME, "German");
        value2.put(SpokenLanguageContract._ID, "es");
        value2.put(SpokenLanguageContract.COLUMN_NAME, "Spanish");
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canDelete() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                SpokenLanguageEntity.buildUri(LanguageCode.en).toString());

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(inserted, null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void delete_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                SpokenLanguageEntity.buildUri(LanguageCode.en).toString());

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                SpokenLanguageEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(inserted, null, null);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(inserted);
    }

    @Test
    public void delete_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                SpokenLanguageEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(1);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().delete(
                inserted, null, null);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                SpokenLanguageEntity.CONTENT_URI,
                values);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                SpokenLanguageEntity.CONTENT_URI);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().delete(
                inserted, null, null);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canUpdate() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                SpokenLanguageEntity.buildUri(LanguageCode.en).toString());

        values.put(SpokenLanguageContract.COLUMN_NAME, "Anglais");

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted,
                        values,
                        null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        SpokenLanguageCursor spokenLanguageCursor = new SpokenLanguageCursor(cursor);
        assertThat(spokenLanguageCursor.moveToFirst()).isTrue();
        SpokenLanguage spokenLanguage = spokenLanguageCursor.getSpokenLanguage();
        assertThat(spokenLanguage.getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(spokenLanguage.getName()).isEqualTo("Anglais");
        assertThat(spokenLanguageCursor.moveToNext()).isFalse();
    }

    @Test
    public void update_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        values.put(SpokenLanguageContract.COLUMN_NAME, "Anglais");

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                SpokenLanguageEntity.CONTENT_URI,
                true,
                observer);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted,
                        values,
                        null, null);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(inserted);
    }

    @Test
    public void update_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(SpokenLanguageContract._ID, "en");
        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                SpokenLanguageEntity.CONTENT_URI,
                values);

        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                SpokenLanguageEntity.CONTENT_URI, null, null, null, null);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        values.put(SpokenLanguageContract.COLUMN_NAME, "Anglais");
        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().update(
                inserted, values, null, null);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                SpokenLanguageEntity.CONTENT_URI);

        values.put(SpokenLanguageContract.COLUMN_NAME, "English");
        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().update(
                inserted, values, null, null);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    private static class CountDownObserver extends ContentObserver
    {
        @NonNull private final CountDownLatch latch;
        public Uri latestChanged;

        public CountDownObserver(
                @Nullable Handler handler,
                @NonNull CountDownLatch latch)
        {
            super(handler);
            this.latch = latch;
        }

        @Override public void onChange(boolean selfChange, Uri uri)
        {
            super.onChange(selfChange, uri);
            latestChanged = uri;
            latch.countDown();
        }
    }
}
