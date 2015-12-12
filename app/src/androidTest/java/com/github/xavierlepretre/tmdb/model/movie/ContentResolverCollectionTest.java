package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.TmdbContract.CollectionEntity;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class ContentResolverCollectionTest
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
                .delete(CollectionEntity.CONTENT_URI, null, null);
    }

    @Test
    public void canGetTypes() throws Exception
    {
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                CollectionEntity.CONTENT_URI))
                .isEqualTo(CollectionEntity.CONTENT_DIR_TYPE);
    }

    @Test
    public void canInsertQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                CollectionEntity.buildUri(new CollectionId(645)).toString());

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        CollectionCursor collectionCursor = new CollectionCursor(cursor);
        assertThat(collectionCursor.moveToFirst()).isTrue();
        Collection collection = collectionCursor.getCollection();
        assertThat(collection.getBackdropPath()).isEqualTo(new ImagePath("/dOSECZImeyZldoq0ObieBE0lwie.jpg"));
        assertThat(collection.getId()).isEqualTo(new CollectionId(645L));
        assertThat(collection.getName()).isEqualTo("James Bond Collection");
        assertThat(collection.getPosterPath()).isEqualTo(new ImagePath("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg"));
        assertThat(collectionCursor.moveToNext()).isFalse();
    }

    @Test
    public void insert_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                CollectionEntity.CONTENT_URI,
                true,
                observer);

        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(inserted);
    }

    @Test
    public void canInsertBulk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        value1.put(CollectionContract._ID, 645L);
        value1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        value1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        ContentValues value2 = new ContentValues();
        value2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        value2.put(CollectionContract._ID, 646L);
        value2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        value2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                CollectionEntity.CONTENT_URI,
                values))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                CollectionEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        CollectionCursor collectionCursor = new CollectionCursor(cursor);
        assertThat(collectionCursor.moveToFirst()).isTrue();
        Collection collection = collectionCursor.getCollection();
        assertThat(collection.getBackdropPath()).isEqualTo(new ImagePath("/dOSECZImeyZldoq0ObieBE0lwie.jpg"));
        assertThat(collection.getId()).isEqualTo(new CollectionId(645L));
        assertThat(collection.getName()).isEqualTo("James Bond Collection");
        assertThat(collection.getPosterPath()).isEqualTo(new ImagePath("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg"));

        assertThat(collectionCursor.moveToNext()).isTrue();
        collection = collectionCursor.getCollection();
        assertThat(collection.getBackdropPath()).isEqualTo(new ImagePath("/other_backdrop.jpg"));
        assertThat(collection.getId()).isEqualTo(new CollectionId(646L));
        assertThat(collection.getName()).isEqualTo("Other Collection");
        assertThat(collection.getPosterPath()).isEqualTo(new ImagePath("/other_poster.jpg"));
        assertThat(collectionCursor.moveToNext()).isFalse();
    }

    @Test
    public void canInsertBulkWithEmptyArray() throws Exception
    {
        ContentValues[] values = new ContentValues[0];

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                CollectionEntity.CONTENT_URI,
                values))
                .isEqualTo(0);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                CollectionEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void insertBulkTheSame_skips1() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        value1.put(CollectionContract._ID, 645L);
        value1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        value1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        ContentValues value2 = new ContentValues();
        value2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        value2.put(CollectionContract._ID, 646L);
        value2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        value2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        ContentValues[] values = new ContentValues[]{value1, value2};
        ContentValues value3 = new ContentValues();
        value3.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        value3.put(CollectionContract._ID, 645L);
        value3.put(CollectionContract.COLUMN_NAME, "James Bond Other Collection");
        value3.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                CollectionEntity.CONTENT_URI,
                values))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                CollectionEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        CollectionCursor collectionCursor = new CollectionCursor(cursor);
        assertThat(collectionCursor.moveToFirst()).isTrue();
        Collection collection = collectionCursor.getCollection();
        assertThat(collection.getBackdropPath()).isEqualTo(new ImagePath("/dOSECZImeyZldoq0ObieBE0lwie.jpg"));
        assertThat(collection.getId()).isEqualTo(new CollectionId(645L));
        assertThat(collection.getName()).isEqualTo("James Bond Collection");
        assertThat(collection.getPosterPath()).isEqualTo(new ImagePath("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg"));

        assertThat(collectionCursor.moveToNext()).isTrue();
        collection = collectionCursor.getCollection();
        assertThat(collection.getBackdropPath()).isEqualTo(new ImagePath("/other_backdrop.jpg"));
        assertThat(collection.getId()).isEqualTo(new CollectionId(646L));
        assertThat(collection.getName()).isEqualTo("Other Collection");
        assertThat(collection.getPosterPath()).isEqualTo(new ImagePath("/other_poster.jpg"));
        assertThat(collectionCursor.moveToNext()).isFalse();
    }

    @Test
    public void insertBulk_getsNotified() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        value1.put(CollectionContract._ID, 645L);
        value1.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        value1.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        ContentValues value2 = new ContentValues();
        value2.put(CollectionContract.COLUMN_BACKDROP_PATH, "/other_backdrop.jpg");
        value2.put(CollectionContract._ID, 646L);
        value2.put(CollectionContract.COLUMN_NAME, "Other Collection");
        value2.put(CollectionContract.COLUMN_POSTER_PATH, "/other_poster.jpg");
        ContentValues[] values = new ContentValues[]{value1, value2};

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                CollectionEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                CollectionEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(CollectionEntity.CONTENT_URI);
    }

    @Test
    public void canDelete() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                CollectionEntity.buildUri(new CollectionId(645)).toString());

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
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                CollectionEntity.buildUri(new CollectionId(645)).toString());

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                CollectionEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(inserted, null, null);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(inserted);
    }

    @Test
    public void canUpdate() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                CollectionEntity.buildUri(new CollectionId(645)).toString());

        values.put(CollectionContract.COLUMN_NAME, "Other Collection");

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted,
                        values,
                        null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        CollectionCursor collectionCursor = new CollectionCursor(cursor);
        assertThat(collectionCursor.moveToFirst()).isTrue();
        Collection collection = collectionCursor.getCollection();
        assertThat(collection.getBackdropPath()).isEqualTo(new ImagePath("/dOSECZImeyZldoq0ObieBE0lwie.jpg"));
        assertThat(collection.getId()).isEqualTo(new CollectionId(645L));
        assertThat(collection.getName()).isEqualTo("Other Collection");
        assertThat(collection.getPosterPath()).isEqualTo(new ImagePath("/HORpg5CSkmeQlAolx3bKMrKgfi.jpg"));
        assertThat(collectionCursor.moveToNext()).isFalse();
    }

    @Test
    public void update_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(CollectionContract.COLUMN_BACKDROP_PATH, "/dOSECZImeyZldoq0ObieBE0lwie.jpg");
        values.put(CollectionContract._ID, 645L);
        values.put(CollectionContract.COLUMN_NAME, "James Bond Collection");
        values.put(CollectionContract.COLUMN_POSTER_PATH, "/HORpg5CSkmeQlAolx3bKMrKgfi.jpg");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                values);

        values.put(CollectionContract.COLUMN_NAME, "Other Collection");

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                CollectionEntity.CONTENT_URI,
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
