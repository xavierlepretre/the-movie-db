package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.TmdbContract.CollectionEntity;
import com.github.xavierlepretre.tmdb.model.TmdbContract.MovieEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class ContentResolverMovieTest
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
            InstrumentationRegistry.getTargetContext().getContentResolver().unregisterContentObserver(observer);
        }
        observers.clear();
    }

    private void deleteFromTable()
    {
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(MovieEntity.CONTENT_URI, null, null);
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(CollectionEntity.CONTENT_URI, null, null);
    }

    @Test
    public void canGetTypes() throws Exception
    {
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                MovieEntity.CONTENT_URI))
                .isEqualTo(MovieEntity.CONTENT_DIR_TYPE);
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                MovieEntity.buildUri(new MovieId(12))))
                .isEqualTo(MovieEntity.CONTENT_ITEM_TYPE);
    }

    @Test
    public void canInsertQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                MovieEntity.buildUri(new MovieId(3)).toString());

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieCursor movieCursor = new MovieCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        Movie movie = movieCursor.getMovie();
        assertThat(movie.getId()).isEqualTo(new MovieId(3));
        assertThat(movie.getTitle()).isEqualTo("title1");
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifNoCollection_cannotInsert() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 1);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);
    }

    @Test
    public void ifHasCollection_canInsert() throws Exception
    {
        ContentValues collectionValues = new ContentValues();
        collectionValues.put(CollectionContract._ID, 1);
        collectionValues.put(CollectionContract.COLUMN_NAME, "collection1");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                collectionValues);
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 1);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                MovieEntity.buildUri(new MovieId(3)).toString());

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieCursor movieCursor = new MovieCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        Movie movie = movieCursor.getMovie();
        assertThat(movie.getId()).isEqualTo(new MovieId(3));
        assertThat(movie.getBelongsToCollectionId()).isEqualTo(new CollectionId(1));
        assertThat(movie.getTitle()).isEqualTo("title1");
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void insert_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer);

        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
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
                MovieEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(0);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                MovieEntity.CONTENT_URI);

        values.put(MovieContract._ID, 4);
        values.put(MovieContract.COLUMN_TITLE, "title2");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canInsertBulk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieContract._ID, 3);
        value1.put(MovieContract.COLUMN_TITLE, "title1");
        ContentValues value2 = new ContentValues();
        value2.put(MovieContract._ID, 4);
        value2.put(MovieContract.COLUMN_TITLE, "title2");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieEntity.CONTENT_URI,
                values))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieCursor movieCursor = new MovieCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        Movie movie = movieCursor.getMovie();
        assertThat(movie.getId()).isEqualTo(new MovieId(3));
        assertThat(movie.getTitle()).isEqualTo("title1");
        assertThat(movieCursor.moveToNext()).isTrue();
        movie = movieCursor.getMovie();
        assertThat(movie.getId()).isEqualTo(new MovieId(4));
        assertThat(movie.getTitle()).isEqualTo("title2");
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void canInsertBulkWithEmptyArray() throws Exception
    {
        ContentValues[] values = new ContentValues[0];

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieEntity.CONTENT_URI,
                values))
                .isEqualTo(0);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void insertBulkTheSame_lastWins() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieContract._ID, 3);
        value1.put(MovieContract.COLUMN_TITLE, "title1");
        ContentValues value2 = new ContentValues();
        value2.put(MovieContract._ID, 3);
        value2.put(MovieContract.COLUMN_TITLE, "title2");
        ContentValues value3 = new ContentValues();
        value3.put(MovieContract._ID, 4);
        value3.put(MovieContract.COLUMN_TITLE, "title3");
        ContentValues[] values = new ContentValues[]{value1, value2, value3};

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieEntity.CONTENT_URI,
                values))
                .isEqualTo(3);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieCursor movieCursor = new MovieCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        Movie movie = movieCursor.getMovie();
        assertThat(movie.getId()).isEqualTo(new MovieId(3));
        assertThat(movie.getTitle()).isEqualTo("title2");
        assertThat(movieCursor.moveToNext()).isTrue();
        movie = movieCursor.getMovie();
        assertThat(movie.getId()).isEqualTo(new MovieId(4));
        assertThat(movie.getTitle()).isEqualTo("title3");
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifNoCollection_cannotInsertBulk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieContract._ID, 3);
        value1.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 1);
        value1.put(MovieContract.COLUMN_TITLE, "title1");
        ContentValues value2 = new ContentValues();
        value2.put(MovieContract._ID, 4);
        value2.put(MovieContract.COLUMN_TITLE, "title2");
        ContentValues[] values = new ContentValues[]{value1, value2};

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieEntity.CONTENT_URI,
                values);
    }

    @Test
    public void ifHasCollection_canInsertBulk() throws Exception
    {
        ContentValues collectionValues = new ContentValues();
        collectionValues.put(CollectionContract._ID, 1);
        collectionValues.put(CollectionContract.COLUMN_NAME, "collection1");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                collectionValues);
        ContentValues value1 = new ContentValues();
        value1.put(MovieContract._ID, 3);
        value1.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 1);
        value1.put(MovieContract.COLUMN_TITLE, "title1");
        ContentValues value2 = new ContentValues();
        value2.put(MovieContract._ID, 4);
        value2.put(MovieContract.COLUMN_TITLE, "title2");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieEntity.CONTENT_URI,
                values))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieCursor movieCursor = new MovieCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        Movie movie = movieCursor.getMovie();
        assertThat(movie.getId()).isEqualTo(new MovieId(3));
        assertThat(movie.getBelongsToCollectionId()).isEqualTo(new CollectionId(1));
        assertThat(movie.getTitle()).isEqualTo("title1");
        assertThat(movieCursor.moveToNext()).isTrue();
        movie = movieCursor.getMovie();
        assertThat(movie.getId()).isEqualTo(new MovieId(4));
        assertThat(movie.getTitle()).isEqualTo("title2");
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void insertBulk_getsNotified() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieContract._ID, 3);
        value1.put(MovieContract.COLUMN_TITLE, "title1");
        ContentValues value2 = new ContentValues();
        value2.put(MovieContract._ID, 4);
        value2.put(MovieContract.COLUMN_TITLE, "title2");
        ContentValues[] values = new ContentValues[]{value1, value2};

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.latestChanged).isEqualTo(MovieEntity.CONTENT_URI);
    }

    @Test
    public void insertBulk_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(0);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        ContentValues value1 = new ContentValues();
        value1.put(MovieContract._ID, 3);
        value1.put(MovieContract.COLUMN_TITLE, "title1");
        ContentValues value2 = new ContentValues();
        value2.put(MovieContract._ID, 4);
        value2.put(MovieContract.COLUMN_TITLE, "title2");
        ContentValues[] values = new ContentValues[]{value1, value2};
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieEntity.CONTENT_URI,
                values);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                MovieEntity.CONTENT_URI);

        value1.put(MovieContract._ID, 5);
        value1.put(MovieContract.COLUMN_TITLE, "title3");
        value2.put(MovieContract._ID, 6);
        value2.put(MovieContract.COLUMN_TITLE, "title4");
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieEntity.CONTENT_URI,
                values);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canDelete() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                MovieEntity.buildUri(new MovieId(3)).toString());

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(inserted, null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void ifHasCollection_canDelete() throws Exception
    {
        ContentValues collectionValues = new ContentValues();
        collectionValues.put(CollectionContract._ID, 1);
        collectionValues.put(CollectionContract.COLUMN_NAME, "collection1");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                collectionValues);
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 1);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                MovieEntity.buildUri(new MovieId(3)).toString());


        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(inserted, null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifHasCollection_cannotDeleteCollection() throws Exception
    {
        ContentValues collectionValues = new ContentValues();
        collectionValues.put(CollectionContract._ID, 1);
        collectionValues.put(CollectionContract.COLUMN_NAME, "collection1");
        Uri insertedCollection = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                collectionValues);
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 1);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                MovieEntity.buildUri(new MovieId(3)).toString());


        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(insertedCollection, null, null);
    }

    @Test
    public void delete_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                MovieEntity.buildUri(new MovieId(3)).toString());

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
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
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieEntity.CONTENT_URI, null, null, null, null);
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
                MovieEntity.CONTENT_URI,
                values);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                MovieEntity.CONTENT_URI);

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
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                MovieEntity.buildUri(new MovieId(3)).toString());

        values.put(MovieContract.COLUMN_TITLE, "title2");

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted, values, null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieCursor movieCursor = new MovieCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        Movie movie = movieCursor.getMovie();
        assertThat(movie.getId()).isEqualTo(new MovieId(3));
        assertThat(movie.getTitle()).isEqualTo("title2");
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void ifHasCollection_canUpdateToNull() throws Exception
    {
        ContentValues collectionValues = new ContentValues();
        collectionValues.put(CollectionContract._ID, 1);
        collectionValues.put(CollectionContract.COLUMN_NAME, "collection1");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                collectionValues);
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 1);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                MovieEntity.buildUri(new MovieId(3)).toString());

        values.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, (Integer) null);

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted, values, null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieCursor movieCursor = new MovieCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        Movie movie = movieCursor.getMovie();
        assertThat(movie.getId()).isEqualTo(new MovieId(3));
        assertThat(movie.getBelongsToCollectionId()).isNull();
        assertThat(movie.getTitle()).isEqualTo("title1");
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifHasCollection_cannotUpdateToOther() throws Exception
    {
        ContentValues collectionValues = new ContentValues();
        collectionValues.put(CollectionContract._ID, 1);
        collectionValues.put(CollectionContract.COLUMN_NAME, "collection1");
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                collectionValues);
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 1);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                MovieEntity.buildUri(new MovieId(3)).toString());

        values.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 2);

        InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted, values, null, null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifHasCollection_cannotUpdateCollectionId() throws Exception
    {
        ContentValues collectionValues = new ContentValues();
        collectionValues.put(CollectionContract._ID, 1);
        collectionValues.put(CollectionContract.COLUMN_NAME, "collection1");
        Uri insertedCollection = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                CollectionEntity.CONTENT_URI,
                collectionValues);
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 1);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        assertThat(inserted).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo(
                MovieEntity.buildUri(new MovieId(3)).toString());

        collectionValues.put(CollectionContract._ID, 2);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(insertedCollection, collectionValues, null, null);
    }

    @Test
    public void update_getsNotified() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        values.put(MovieContract.COLUMN_TITLE, "title2");

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
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
        values.put(MovieContract._ID, 3);
        values.put(MovieContract.COLUMN_TITLE, "title1");
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieEntity.CONTENT_URI,
                values);

        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieEntity.CONTENT_URI, null, null, null, null);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        values.put(MovieContract.COLUMN_TITLE, "title2");
        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().update(
                inserted, values, null, null);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                MovieEntity.CONTENT_URI);

        values.put(MovieContract.COLUMN_TITLE, "title3");
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
