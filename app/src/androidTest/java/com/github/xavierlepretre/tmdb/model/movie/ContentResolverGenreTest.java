package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntityProviderDelegate;
import com.github.xavierlepretre.tmdb.model.TmdbContract.GenreEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class ContentResolverGenreTest
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
                .delete(GenreEntity.CONTENT_URI, null, null);
    }

    @Test
    public void canGetTypes() throws Exception
    {
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                GenreEntity.CONTENT_URI))
                .isEqualTo(GenreEntity.CONTENT_DIR_TYPE);
    }

    @Test
    public void canInsertQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                GenreEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                GenreEntity.CONTENT_URI.buildUpon()
                        .appendPath("3")
                        .build().toString());

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        GenreCursor genreCursor = new GenreCursor(cursor);
        assertThat(genreCursor.moveToFirst()).isTrue();
        Genre genre = genreCursor.getGenre();
        assertThat(genre.getId()).isEqualTo(new GenreId(3));
        assertThat(genre.getName()).isEqualTo("Adventure");
        assertThat(genreCursor.moveToNext()).isFalse();
    }

    @Test
    public void insert_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer);

        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                GenreEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(inserted);
    }

    @Test
    public void canInsertBulk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(GenreContract._ID, 3);
        value1.put(GenreContract.COLUMN_NAME, "Adventure");
        ContentValues value2 = new ContentValues();
        value2.put(GenreContract._ID, 4);
        value2.put(GenreContract.COLUMN_NAME, "Comic");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                GenreEntity.CONTENT_URI,
                values))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                GenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        GenreCursor genreCursor = new GenreCursor(cursor);
        assertThat(genreCursor.moveToFirst()).isTrue();
        Genre genre = genreCursor.getGenre();
        assertThat(genre.getId()).isEqualTo(new GenreId(3));
        assertThat(genre.getName()).isEqualTo("Adventure");
        assertThat(genreCursor.moveToNext()).isTrue();
        genre = genreCursor.getGenre();
        assertThat(genre.getId()).isEqualTo(new GenreId(4));
        assertThat(genre.getName()).isEqualTo("Comic");
        assertThat(genreCursor.moveToNext()).isFalse();
    }

    @Test
    public void canInsertBulkWithEmptyArray() throws Exception
    {
        ContentValues[] values = new ContentValues[0];

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                GenreEntity.CONTENT_URI,
                values))
                .isEqualTo(0);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                GenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void insertBulkTheSame_skips1() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(GenreContract._ID, 3);
        value1.put(GenreContract.COLUMN_NAME, "Adventure");
        ContentValues value2 = new ContentValues();
        value2.put(GenreContract._ID, 4);
        value2.put(GenreContract.COLUMN_NAME, "Comic");
        ContentValues[] values = new ContentValues[]{value1, value2};
        ContentValues value3 = new ContentValues();
        value3.put(GenreContract._ID, 3);
        value3.put(GenreContract.COLUMN_NAME, "Action");

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                GenreEntity.CONTENT_URI,
                values))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                GenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        GenreCursor genreCursor = new GenreCursor(cursor);
        assertThat(genreCursor.moveToFirst()).isTrue();
        Genre genre = genreCursor.getGenre();
        assertThat(genre.getId()).isEqualTo(new GenreId(3));
        assertThat(genre.getName()).isEqualTo("Adventure");
        assertThat(genreCursor.moveToNext()).isTrue();
        genre = genreCursor.getGenre();
        assertThat(genre.getId()).isEqualTo(new GenreId(4));
        assertThat(genre.getName()).isEqualTo("Comic");
        assertThat(genreCursor.moveToNext()).isFalse();
    }

    @Test
    public void insertBulk_getsNotified() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(GenreContract._ID, 3);
        value1.put(GenreContract.COLUMN_NAME, "Adventure");
        ContentValues value2 = new ContentValues();
        value2.put(GenreContract._ID, 4);
        value2.put(GenreContract.COLUMN_NAME, "Comic");
        ContentValues[] values = new ContentValues[]{value1, value2};

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                GenreEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(GenreEntity.CONTENT_URI);
    }

    @Test
    public void canDelete() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                GenreEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                GenreEntity.CONTENT_URI.buildUpon()
                        .appendPath("3")
                        .build().toString());

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
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                GenreEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                GenreEntity.CONTENT_URI.buildUpon()
                        .appendPath("3")
                        .build().toString());

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
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
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                GenreEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                GenreEntity.CONTENT_URI.buildUpon()
                        .appendPath("3")
                        .build().toString());

        values.put(GenreContract.COLUMN_NAME, "Comic");

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted,
                        values,
                        null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        GenreCursor genreCursor = new GenreCursor(cursor);
        assertThat(genreCursor.moveToFirst()).isTrue();
        Genre genre = genreCursor.getGenre();
        assertThat(genre.getId()).isEqualTo(new GenreId(3));
        assertThat(genre.getName()).isEqualTo("Comic");
        assertThat(genreCursor.moveToNext()).isFalse();
    }

    @Test
    public void update_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(GenreContract._ID, 3);
        values.put(GenreContract.COLUMN_NAME, "Adventure");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                GenreEntity.CONTENT_URI,
                values);

        values.put(GenreContract.COLUMN_NAME, "Comic");

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
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
