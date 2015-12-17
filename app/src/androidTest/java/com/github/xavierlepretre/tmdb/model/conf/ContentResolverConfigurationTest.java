package com.github.xavierlepretre.tmdb.model.conf;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.TmdbContract;
import com.github.xavierlepretre.tmdb.model.TmdbContract.ConfigurationEntity;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract.ImagesConfSegment;
import com.github.xavierlepretre.tmdb.sql.conf.ConfigurationCursor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class ContentResolverConfigurationTest
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
                .delete(ConfigurationEntity.CONTENT_URI, null, null);
    }

    @Test
    public void canGetTypes() throws Exception
    {
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                ConfigurationEntity.CONTENT_URI))
                .isEqualTo(ConfigurationEntity.CONTENT_ITEM_TYPE);
    }

    @Test
    public void canInsertQuery() throws Exception
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
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ConfigurationEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                ConfigurationEntity.CONTENT_URI.toString());

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        ConfigurationCursor configurationCursor = new ConfigurationCursor(cursor);
        assertThat(configurationCursor.moveToFirst()).isTrue();
        Configuration configuration = configurationCursor.getConfiguration();
        assertThat(configuration.getImagesConf().getBaseUrl()).isEqualTo("url1");
        assertThat(configuration.getImagesConf().getSecureBaseUrl()).isEqualTo("url2");
        assertThat(configuration.getImagesConf().getBackdropSizes()).isEqualTo(Arrays.asList(new ImageSize("w1"), new ImageSize("w2")));
        assertThat(configuration.getImagesConf().getLogoSizes()).isEqualTo(Arrays.asList(new ImageSize("w3"), new ImageSize("w4")));
        assertThat(configuration.getImagesConf().getPosterSizes()).isEqualTo(Arrays.asList(new ImageSize("w5"), new ImageSize("w6")));
        assertThat(configuration.getImagesConf().getProfileSizes()).isEqualTo(Arrays.asList(new ImageSize("w7"), new ImageSize("w8")));
        assertThat(configuration.getImagesConf().getStillSizes()).isEqualTo(Arrays.asList(new ImageSize("w9"), new ImageSize("w10")));
        assertThat(configuration.getChangeKeys()).isEqualTo(Arrays.asList(new ChangeKey("key1"), new ChangeKey("key2")));
        assertThat(configurationCursor.moveToNext()).isFalse();
    }

    @Test
    public void insert_getsNotified() throws Exception
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
        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                ConfigurationEntity.CONTENT_URI,
                true,
                observer);

        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ConfigurationEntity.CONTENT_URI,
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
                TmdbContract.ConfigurationEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(0);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        ContentValues values = new ContentValues();
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ConfigurationEntity.CONTENT_URI,
                values);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                TmdbContract.ConfigurationEntity.CONTENT_URI);

        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url3");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ConfigurationEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cannotInsertBulk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        ContentValues value2 = new ContentValues();
        ContentValues[] values = new ContentValues[]{value1, value2};

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                ConfigurationEntity.CONTENT_URI,
                values);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cannotInsertBulkWithEmptyArray() throws Exception
    {
        ContentValues[] values = new ContentValues[0];

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                ConfigurationEntity.CONTENT_URI,
                values);
    }

    @Test
    public void canDelete() throws Exception
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
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ConfigurationEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(ConfigurationEntity.CONTENT_URI.toString());

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
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ConfigurationEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(ConfigurationEntity.CONTENT_URI.toString());

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                ConfigurationEntity.CONTENT_URI,
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
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ConfigurationEntity.CONTENT_URI,
                values);

        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                TmdbContract.ConfigurationEntity.CONTENT_URI, null, null, null, null);
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
                TmdbContract.ConfigurationEntity.CONTENT_URI,
                values);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                TmdbContract.ConfigurationEntity.CONTENT_URI);

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
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ConfigurationEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(ConfigurationEntity.CONTENT_URI.toString());

        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url3");

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted,
                        values,
                        null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        ConfigurationCursor configurationCursor = new ConfigurationCursor(cursor);
        assertThat(configurationCursor.moveToFirst()).isTrue();
        Configuration configuration = configurationCursor.getConfiguration();
        assertThat(configuration.getImagesConf().getBaseUrl()).isEqualTo("url3");
        assertThat(configuration.getImagesConf().getSecureBaseUrl()).isEqualTo("url2");
        assertThat(configuration.getImagesConf().getBackdropSizes()).isEqualTo(Arrays.asList(new ImageSize("w1"), new ImageSize("w2")));
        assertThat(configuration.getImagesConf().getLogoSizes()).isEqualTo(Arrays.asList(new ImageSize("w3"), new ImageSize("w4")));
        assertThat(configuration.getImagesConf().getPosterSizes()).isEqualTo(Arrays.asList(new ImageSize("w5"), new ImageSize("w6")));
        assertThat(configuration.getImagesConf().getProfileSizes()).isEqualTo(Arrays.asList(new ImageSize("w7"), new ImageSize("w8")));
        assertThat(configuration.getImagesConf().getStillSizes()).isEqualTo(Arrays.asList(new ImageSize("w9"), new ImageSize("w10")));
        assertThat(configuration.getChangeKeys()).isEqualTo(Arrays.asList(new ChangeKey("key1"), new ChangeKey("key2")));
        assertThat(configurationCursor.moveToNext()).isFalse();
    }

    @Test
    public void update_getsNotified() throws Exception
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
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                ConfigurationEntity.CONTENT_URI,
                values);

        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url3");

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                ConfigurationEntity.CONTENT_URI,
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
        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
        values.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, "url2");
        values.put(ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2");
        values.put(ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4");
        values.put(ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6");
        values.put(ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8");
        values.put(ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10");
        values.put(ConfigurationContract.COLUMN_CHANGE_KEYS, "key1,key2");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                TmdbContract.ConfigurationEntity.CONTENT_URI,
                values);

        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                TmdbContract.ConfigurationEntity.CONTENT_URI, null, null, null, null);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url3");
        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().update(
                inserted, values, null, null);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                TmdbContract.ConfigurationEntity.CONTENT_URI);

        values.put(ImagesConfSegment.COLUMN_BASE_URL, "url1");
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
