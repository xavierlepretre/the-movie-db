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

import com.github.xavierlepretre.tmdb.model.TmdbContract.GenreEntity;
import com.github.xavierlepretre.tmdb.model.TmdbContract.MovieEntity;
import com.github.xavierlepretre.tmdb.model.TmdbContract.MovieGenreEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;

public class ContentResolverMovieGenreTest
{
    private Cursor cursor;
    private List<ContentObserver> observers;

    @Before
    public void setUp() throws Exception
    {
        deleteFromTable();
        observers = new ArrayList<>();

        ContentValues movie1 = new ContentValues();
        movie1.put(MovieContract._ID, 1);
        movie1.put(MovieContract.COLUMN_TITLE, "title1");
        ContentValues movie2 = new ContentValues();
        movie2.put(MovieContract._ID, 3);
        movie2.put(MovieContract.COLUMN_TITLE, "title3");
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieEntity.CONTENT_URI,
                new ContentValues[]{movie1, movie2});

        ContentValues genre1 = new ContentValues();
        genre1.put(GenreContract._ID, 2);
        genre1.put(GenreContract.COLUMN_NAME, "genre2");
        ContentValues genre2 = new ContentValues();
        genre2.put(GenreContract._ID, 4);
        genre2.put(GenreContract.COLUMN_NAME, "genre4");
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                GenreEntity.CONTENT_URI,
                new ContentValues[]{genre1, genre2});
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
                .delete(MovieGenreEntity.CONTENT_URI, null, null);
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(MovieEntity.CONTENT_URI, null, null);
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(GenreEntity.CONTENT_URI, null, null);
    }

    @Test
    public void canGetTypes() throws Exception
    {
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                MovieGenreEntity.CONTENT_URI))
                .isEqualTo(MovieGenreEntity.CONTENT_DIR_TYPE);
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(12), new MovieId(13))))
                .isEqualTo(MovieGenreEntity.CONTENT_ITEM_TYPE);
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(12))))
                .isEqualTo(MovieEntity.CONTENT_DIR_TYPE);
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().getType(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(12))))
                .isEqualTo(GenreEntity.CONTENT_DIR_TYPE);
    }

    @Test
    public void ifHasMovieAndGenre_canInsert_andQuery() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        Uri inserted1 = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);

        assertThat(inserted1).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted1.toString()).isEqualTo(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)).toString());

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted1, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movieGenre = movieCursor.getMovieGenre();
        assertThat(movieGenre.getGenreId()).isEqualTo(new GenreId(2));
        assertThat(movieGenre.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void ifHasMovieAndGenre_canInsertByGenre_andQuery() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        Uri inserted1 = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                value1);

        assertThat(inserted1).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted1.toString()).isEqualTo(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)).toString());

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted1, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movieGenre = movieCursor.getMovieGenre();
        assertThat(movieGenre.getGenreId()).isEqualTo(new GenreId(2));
        assertThat(movieGenre.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void ifHasMovieAndGenre_canInsertByMovie_andQuery() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        Uri inserted1 = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                value1);

        assertThat(inserted1).isNotNull();
        //noinspection ConstantConditions
        assertThat(inserted1.toString()).isEqualTo(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)).toString());

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                inserted1, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movieGenre = movieCursor.getMovieGenre();
        assertThat(movieGenre.getGenreId()).isEqualTo(new GenreId(2));
        assertThat(movieGenre.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void insertOnMovieGenreById_fails() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                value1);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifNoMovie_cannotInsert() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifNoGenre_cannotInsert() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifNoMovie_cannotInsertByGenre() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                value1);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifNoGenre_cannotInsertByGenre() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(1)),
                value1);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifNoMovie_cannotInsertByMovie() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(2)),
                value1);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifNoGenre_cannotInsertByMovie() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                value1);
    }

    @Test
    public void insert_getsNotifiedOn4Uris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        final CountDownLatch deliverSignal = new CountDownLatch(4);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.changedSet).containsOnly(
                MovieGenreEntity.CONTENT_URI,
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)));
    }

    @Test
    public void insertForGenre_getsNotifiedOn4Uris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        final CountDownLatch deliverSignal = new CountDownLatch(4);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                value1);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.changedSet).containsOnly(
                MovieGenreEntity.CONTENT_URI,
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)));
    }

    @Test
    public void insertForMovie_getsNotifiedOn4Uris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        final CountDownLatch deliverSignal = new CountDownLatch(4);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                value1);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.changedSet).containsOnly(
                MovieGenreEntity.CONTENT_URI,
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)));
    }

    @Test
    public void insert_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(0);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                MovieGenreEntity.CONTENT_URI);

        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canInsertBulk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2}))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(2));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isTrue();
        movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(4));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void canInsertBulkByGenre() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                new ContentValues[]{value1, value2}))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(2));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isTrue();
        movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(2));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(3));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void canInsertBulkByMovie() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                new ContentValues[]{value1, value2}))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(2));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isTrue();
        movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(4));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void bulkInsertOnMovieGenreById_fails() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                new ContentValues[]{value1, value2});
    }

    @Test
    public void canInsertBulkWithEmptyArray() throws Exception
    {
        ContentValues[] values = new ContentValues[0];

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                values))
                .isEqualTo(0);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void canInsertBulkByGenreWithEmptyArray() throws Exception
    {
        ContentValues[] values = new ContentValues[0];

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                values))
                .isEqualTo(0);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void canInsertBulkByMovieWithEmptyArray() throws Exception
    {
        ContentValues[] values = new ContentValues[0];

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                values))
                .isEqualTo(0);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.moveToFirst()).isFalse();
    }

    @Test
    public void insertBulkTheSame_lastWins() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2, value3}))
                .isEqualTo(3);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(2);
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(2));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isTrue();
        movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(4));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    // Not doing the same byGenre and byMovie
    @Test(expected = SQLiteConstraintException.class)
    public void ifNoMovie_cannotInsertBulk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 5);

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1});
    }

    // Not doing the same byGenre and byMovie
    @Test(expected = SQLiteConstraintException.class)
    public void ifNoGenre_cannotInsertBulk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 6);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1});
    }

    @Test
    public void insertBulk_getsNotifiedOnCertainUris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);

        final CountDownLatch deliverSignal = new CountDownLatch(5);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2, value3});

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.changedSet).containsOnly(
                MovieGenreEntity.CONTENT_URI,
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4)),
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                MovieGenreEntity.buildMovieGenreUri(new MovieId(3)));
    }

    @Test
    public void insertBulkByGenre_getsNotifiedOnCertainUris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);

        final CountDownLatch deliverSignal = new CountDownLatch(4);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                new ContentValues[]{value1, value2});

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.changedSet).containsExactly(
                MovieGenreEntity.CONTENT_URI,
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                MovieGenreEntity.buildMovieGenreUri(new MovieId(3)));
    }

    @Test
    public void insertBulkByMovie_getsNotifiedOnCertainUris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);

        final CountDownLatch deliverSignal = new CountDownLatch(4);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer);

        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                new ContentValues[]{value1, value2});

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
        assertThat(observer.changedSet).containsExactly(
                MovieGenreEntity.CONTENT_URI,
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4)),
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)));
    }

    // Not doing the same byGenre and byMovie
    @Test
    public void insertBulk_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        //noinspection ConstantConditions
        assertThat(cursor.getCount()).isEqualTo(0);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2});

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                MovieGenreEntity.CONTENT_URI);

        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2});

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canDeleteWithFullId() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2, value3});

        //noinspection ConstantConditions
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)), null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, MovieGenreEntity.COLUMN_GENRE_ID);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(2);
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(2));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(3));
        assertThat(movieCursor.moveToNext()).isTrue();
        movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(4));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void canDeleteByGenre() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2, value3});

        //noinspection ConstantConditions
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(MovieGenreEntity.buildMovieGenreUri(new GenreId(2)), null, null))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(1);
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(4));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void canDeleteByMovie() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2, value3});

        //noinspection ConstantConditions
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(MovieGenreEntity.buildMovieGenreUri(new MovieId(1)), null, null))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(1);
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(2));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(3));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void canDelete() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2, value3});

        //noinspection ConstantConditions
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(MovieGenreEntity.CONTENT_URI, null, null))
                .isEqualTo(3);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(0);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifHasMovieGenre_cannotDeleteGenre() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(GenreEntity.buildUri(new GenreId(2)), null, null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifHasMovieGenre_cannotDeleteMovie() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(MovieEntity.buildUri(new MovieId(1)), null, null);
    }

    @Test
    public void deleteByFullId_getsNotifiedOnCertainUris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2, value3});

        final CountDownLatch deliverSignal1 = new CountDownLatch(1);
        final CountDownLatch deliverSignal2 = new CountDownLatch(1);
        final CountDownLatch deliverSignal3 = new CountDownLatch(1);
        final CountDownLatch deliverSignal4 = new CountDownLatch(1);
        final CountDownLatch deliverSignal5 = new CountDownLatch(1);
        final CountDownLatch deliverSignal6 = new CountDownLatch(1);
        CountDownObserver observer1 = new CountDownObserver(null, deliverSignal1);
        CountDownObserver observer2 = new CountDownObserver(null, deliverSignal2);
        CountDownObserver observer3 = new CountDownObserver(null, deliverSignal3);
        CountDownObserver observer4 = new CountDownObserver(null, deliverSignal4);
        CountDownObserver observer5 = new CountDownObserver(null, deliverSignal5);
        CountDownObserver observer6 = new CountDownObserver(null, deliverSignal6);
        observers.add(observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer2);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                true,
                observer3);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                true,
                observer4);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer5);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                true,
                observer6);

        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)), null, null);

        deliverSignal1.await(5, TimeUnit.SECONDS);
        deliverSignal2.await(5, TimeUnit.SECONDS);
        deliverSignal3.await(5, TimeUnit.SECONDS);
        deliverSignal4.await(5, TimeUnit.SECONDS);
        deliverSignal5.await(5, TimeUnit.SECONDS);
        deliverSignal6.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal1.getCount()).isEqualTo(0);
        assertThat(deliverSignal2.getCount()).isEqualTo(0);
        assertThat(deliverSignal3.getCount()).isEqualTo(0);
        assertThat(deliverSignal4.getCount()).isEqualTo(0);
        assertThat(deliverSignal5.getCount()).isEqualTo(0);
        assertThat(deliverSignal6.getCount()).isEqualTo(0);
    }

    @Test
    public void deleteByGenreId_getsNotifiedOnCertainUris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2, value3});

        final CountDownLatch deliverSignal1 = new CountDownLatch(1);
        final CountDownLatch deliverSignal2 = new CountDownLatch(1);
        final CountDownLatch deliverSignal3 = new CountDownLatch(1);
        final CountDownLatch deliverSignal4 = new CountDownLatch(1);
        final CountDownLatch deliverSignal5 = new CountDownLatch(1);
        final CountDownLatch deliverSignal6 = new CountDownLatch(1);
        final CountDownLatch deliverSignal7 = new CountDownLatch(1);
        final CountDownLatch deliverSignal8 = new CountDownLatch(1);
        CountDownObserver observer1 = new CountDownObserver(null, deliverSignal1);
        CountDownObserver observer2 = new CountDownObserver(null, deliverSignal2);
        CountDownObserver observer3 = new CountDownObserver(null, deliverSignal3);
        CountDownObserver observer4 = new CountDownObserver(null, deliverSignal4);
        CountDownObserver observer5 = new CountDownObserver(null, deliverSignal5);
        CountDownObserver observer6 = new CountDownObserver(null, deliverSignal6);
        CountDownObserver observer7 = new CountDownObserver(null, deliverSignal7);
        CountDownObserver observer8 = new CountDownObserver(null, deliverSignal8);
        observers.add(observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer2);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                true,
                observer3);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                true,
                observer4);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(3)),
                true,
                observer5);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer6);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                true,
                observer7);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(3)),
                true,
                observer8);

        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(MovieGenreEntity.buildMovieGenreUri(new GenreId(2)), null, null);

        deliverSignal1.await(5, TimeUnit.SECONDS);
        deliverSignal2.await(5, TimeUnit.SECONDS);
        deliverSignal3.await(5, TimeUnit.SECONDS);
        deliverSignal4.await(5, TimeUnit.SECONDS);
        deliverSignal5.await(5, TimeUnit.SECONDS);
        deliverSignal6.await(5, TimeUnit.SECONDS);
        deliverSignal7.await(5, TimeUnit.SECONDS);
        deliverSignal8.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal1.getCount()).isEqualTo(0);
        assertThat(deliverSignal2.getCount()).isEqualTo(0);
        assertThat(deliverSignal3.getCount()).isEqualTo(0);
        assertThat(deliverSignal4.getCount()).isEqualTo(0);
        assertThat(deliverSignal5.getCount()).isEqualTo(0);
        assertThat(deliverSignal6.getCount()).isEqualTo(0);
        assertThat(deliverSignal7.getCount()).isEqualTo(0);
        assertThat(deliverSignal8.getCount()).isEqualTo(0);
    }

    @Test
    public void deleteByMovieId_getsNotifiedOnCertainUris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2, value3});

        final CountDownLatch deliverSignal1 = new CountDownLatch(1);
        final CountDownLatch deliverSignal2 = new CountDownLatch(1);
        final CountDownLatch deliverSignal3 = new CountDownLatch(1);
        final CountDownLatch deliverSignal4 = new CountDownLatch(1);
        final CountDownLatch deliverSignal5 = new CountDownLatch(1);
        final CountDownLatch deliverSignal6 = new CountDownLatch(1);
        final CountDownLatch deliverSignal7 = new CountDownLatch(1);
        final CountDownLatch deliverSignal8 = new CountDownLatch(1);
        CountDownObserver observer1 = new CountDownObserver(null, deliverSignal1);
        CountDownObserver observer2 = new CountDownObserver(null, deliverSignal2);
        CountDownObserver observer3 = new CountDownObserver(null, deliverSignal3);
        CountDownObserver observer4 = new CountDownObserver(null, deliverSignal4);
        CountDownObserver observer5 = new CountDownObserver(null, deliverSignal5);
        CountDownObserver observer6 = new CountDownObserver(null, deliverSignal6);
        CountDownObserver observer7 = new CountDownObserver(null, deliverSignal7);
        CountDownObserver observer8 = new CountDownObserver(null, deliverSignal8);
        observers.add(observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer2);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                true,
                observer3);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                true,
                observer4);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4)),
                true,
                observer5);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4), new MovieId(1)),
                true,
                observer6);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer7);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                true,
                observer8);

        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(MovieGenreEntity.buildMovieGenreUri(new MovieId(1)), null, null);

        deliverSignal1.await(5, TimeUnit.SECONDS);
        deliverSignal2.await(5, TimeUnit.SECONDS);
        deliverSignal3.await(5, TimeUnit.SECONDS);
        deliverSignal4.await(5, TimeUnit.SECONDS);
        deliverSignal5.await(5, TimeUnit.SECONDS);
        deliverSignal6.await(5, TimeUnit.SECONDS);
        deliverSignal7.await(5, TimeUnit.SECONDS);
        deliverSignal8.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal1.getCount()).isEqualTo(0);
        assertThat(deliverSignal2.getCount()).isEqualTo(0);
        assertThat(deliverSignal3.getCount()).isEqualTo(0);
        assertThat(deliverSignal4.getCount()).isEqualTo(0);
        assertThat(deliverSignal5.getCount()).isEqualTo(0);
        assertThat(deliverSignal6.getCount()).isEqualTo(0);
        assertThat(deliverSignal7.getCount()).isEqualTo(0);
        assertThat(deliverSignal8.getCount()).isEqualTo(0);
    }

    @Test
    public void delete_getsNotifiedOnCertainUris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2, value3});

        final CountDownLatch deliverSignal1 = new CountDownLatch(1);
        final CountDownLatch deliverSignal2 = new CountDownLatch(1);
        final CountDownLatch deliverSignal3 = new CountDownLatch(1);
        final CountDownLatch deliverSignal4 = new CountDownLatch(1);
        final CountDownLatch deliverSignal5 = new CountDownLatch(1);
        final CountDownLatch deliverSignal6 = new CountDownLatch(1);
        final CountDownLatch deliverSignal7 = new CountDownLatch(1);
        final CountDownLatch deliverSignal8 = new CountDownLatch(1);
        final CountDownLatch deliverSignal9 = new CountDownLatch(1);
        final CountDownLatch deliverSignal10 = new CountDownLatch(1);
        CountDownObserver observer1 = new CountDownObserver(null, deliverSignal1);
        CountDownObserver observer2 = new CountDownObserver(null, deliverSignal2);
        CountDownObserver observer3 = new CountDownObserver(null, deliverSignal3);
        CountDownObserver observer4 = new CountDownObserver(null, deliverSignal4);
        CountDownObserver observer5 = new CountDownObserver(null, deliverSignal5);
        CountDownObserver observer6 = new CountDownObserver(null, deliverSignal6);
        CountDownObserver observer7 = new CountDownObserver(null, deliverSignal7);
        CountDownObserver observer8 = new CountDownObserver(null, deliverSignal8);
        CountDownObserver observer9 = new CountDownObserver(null, deliverSignal9);
        CountDownObserver observer10 = new CountDownObserver(null, deliverSignal10);
        observers.add(observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer2);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                true,
                observer3);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                true,
                observer4);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4)),
                true,
                observer5);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4), new MovieId(1)),
                true,
                observer6);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer7);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                true,
                observer8);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(3)),
                true,
                observer9);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(3)),
                true,
                observer10);

        InstrumentationRegistry.getTargetContext().getContentResolver()
                .delete(MovieGenreEntity.CONTENT_URI, null, null);

        deliverSignal1.await(5, TimeUnit.SECONDS);
        deliverSignal2.await(5, TimeUnit.SECONDS);
        deliverSignal3.await(5, TimeUnit.SECONDS);
        deliverSignal4.await(5, TimeUnit.SECONDS);
        deliverSignal5.await(5, TimeUnit.SECONDS);
        deliverSignal6.await(5, TimeUnit.SECONDS);
        deliverSignal7.await(5, TimeUnit.SECONDS);
        deliverSignal8.await(5, TimeUnit.SECONDS);
        deliverSignal9.await(5, TimeUnit.SECONDS);
        deliverSignal10.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal1.getCount()).isEqualTo(0);
        assertThat(deliverSignal2.getCount()).isEqualTo(0);
        assertThat(deliverSignal3.getCount()).isEqualTo(0);
        assertThat(deliverSignal4.getCount()).isEqualTo(0);
        assertThat(deliverSignal5.getCount()).isEqualTo(0);
        assertThat(deliverSignal6.getCount()).isEqualTo(0);
        assertThat(deliverSignal7.getCount()).isEqualTo(0);
        assertThat(deliverSignal8.getCount()).isEqualTo(0);
        assertThat(deliverSignal9.getCount()).isEqualTo(0);
        assertThat(deliverSignal10.getCount()).isEqualTo(0);
    }

    // Not doing the same byGenre and byMovie
    @Test
    public void delete_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);

        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
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
                MovieGenreEntity.CONTENT_URI,
                value1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                MovieGenreEntity.CONTENT_URI);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().delete(
                inserted, null, null);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    @Test
    public void canUpdate() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_GENRE_ID, 4);

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(MovieGenreEntity.CONTENT_URI, newValues, null, null))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(2);
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(4));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isTrue();
        movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(4));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(3));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void canUpdateByGenre() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_GENRE_ID, 4);

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(MovieGenreEntity.buildMovieGenreUri(new GenreId(2)), newValues, null, null))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(2);
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(4));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isTrue();
        movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(4));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(3));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void canUpdateByMovie() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(MovieGenreEntity.buildMovieGenreUri(new MovieId(1)), newValues, null, null))
                .isEqualTo(2);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(2);
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(2));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(3));
        assertThat(movieCursor.moveToNext()).isTrue();
        movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(4));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(3));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test
    public void canUpdateByFullId() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);

        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)), newValues, null, null))
                .isEqualTo(1);

        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);
        assertThat(cursor).isNotNull();
        assertThat(cursor.getCount()).isEqualTo(2);
        MovieGenreCursor movieCursor = new MovieGenreCursor(cursor);
        assertThat(movieCursor.moveToFirst()).isTrue();
        MovieGenre movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(2));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(3));
        assertThat(movieCursor.moveToNext()).isTrue();
        movie = movieCursor.getMovieGenre();
        assertThat(movie.getGenreId()).isEqualTo(new GenreId(4));
        assertThat(movie.getMovieId()).isEqualTo(new MovieId(1));
        assertThat(movieCursor.moveToNext()).isFalse();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void cannotUpdateToNonExistingGenre() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);

        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 5);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted, value1, null, null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void cannotUpdateToNonExistingMovie() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        Uri inserted = InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);

        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 6);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(inserted, value1, null, null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifHasMovieGenre_cannotUpdateGenreId() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);

        ContentValues genreValues = new ContentValues();
        genreValues.put(GenreEntity._ID, 6);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(GenreEntity.buildUri(new GenreId(2)), genreValues, null, null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void ifHasMovieGenre_cannotUpdateMovieId() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().insert(
                MovieGenreEntity.CONTENT_URI,
                value1);

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntity._ID, 5);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(MovieEntity.buildUri(new MovieId(1)), movieValues, null, null);
    }

    @Test
    public void updateByFullId_getsNotifiedOnCertainUris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_GENRE_ID, 4);

        final CountDownLatch deliverSignal1 = new CountDownLatch(1);
        final CountDownLatch deliverSignal2 = new CountDownLatch(1);
        final CountDownLatch deliverSignal3 = new CountDownLatch(1);
        final CountDownLatch deliverSignal4 = new CountDownLatch(1);
        final CountDownLatch deliverSignal5 = new CountDownLatch(1);
        final CountDownLatch deliverSignal6 = new CountDownLatch(1);
        final CountDownLatch deliverSignal7 = new CountDownLatch(1);
        final CountDownLatch deliverSignal8 = new CountDownLatch(1);
        CountDownObserver observer1 = new CountDownObserver(null, deliverSignal1);
        CountDownObserver observer2 = new CountDownObserver(null, deliverSignal2);
        CountDownObserver observer3 = new CountDownObserver(null, deliverSignal3);
        CountDownObserver observer4 = new CountDownObserver(null, deliverSignal4);
        CountDownObserver observer5 = new CountDownObserver(null, deliverSignal5);
        CountDownObserver observer6 = new CountDownObserver(null, deliverSignal6);
        CountDownObserver observer7 = new CountDownObserver(null, deliverSignal7);
        CountDownObserver observer8 = new CountDownObserver(null, deliverSignal8);
        observers.add(observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer2);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                true,
                observer3);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                true,
                observer4);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                true,
                observer5);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer6);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4)),
                true,
                observer7);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4), new MovieId(1)),
                true,
                observer8);

        //noinspection ConstantConditions
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                        newValues,
                        null, null)).isEqualTo(1);

        deliverSignal1.await(5, TimeUnit.SECONDS);
        deliverSignal2.await(5, TimeUnit.SECONDS);
        deliverSignal3.await(5, TimeUnit.SECONDS);
        deliverSignal4.await(5, TimeUnit.SECONDS);
        deliverSignal5.await(5, TimeUnit.SECONDS);
        deliverSignal6.await(5, TimeUnit.SECONDS);
        deliverSignal7.await(100, TimeUnit.MILLISECONDS);
        deliverSignal8.await(100, TimeUnit.MILLISECONDS);
        assertThat(deliverSignal1.getCount()).isEqualTo(0);
        assertThat(deliverSignal2.getCount()).isEqualTo(0);
        assertThat(deliverSignal3.getCount()).isEqualTo(0);
        assertThat(deliverSignal4.getCount()).isEqualTo(0);
        assertThat(deliverSignal5.getCount()).isEqualTo(0);
        assertThat(deliverSignal6.getCount()).isEqualTo(0);
        // That's right, we do not expect to be notified on the updated row
        assertThat(deliverSignal7.getCount()).isEqualTo(1);
        assertThat(deliverSignal8.getCount()).isEqualTo(1);
    }

    @Test
    public void updateByGenreId_getsNotifiedOnCertainUris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_GENRE_ID, 4);

        final CountDownLatch deliverSignal1 = new CountDownLatch(1);
        final CountDownLatch deliverSignal2 = new CountDownLatch(1);
        final CountDownLatch deliverSignal3 = new CountDownLatch(1);
        final CountDownLatch deliverSignal4 = new CountDownLatch(1);
        final CountDownLatch deliverSignal5 = new CountDownLatch(1);
        final CountDownLatch deliverSignal6 = new CountDownLatch(1);
        final CountDownLatch deliverSignal7 = new CountDownLatch(1);
        final CountDownLatch deliverSignal8 = new CountDownLatch(1);
        final CountDownLatch deliverSignal9 = new CountDownLatch(1);
        final CountDownLatch deliverSignal10 = new CountDownLatch(1);
        final CountDownLatch deliverSignal11 = new CountDownLatch(1);
        CountDownObserver observer1 = new CountDownObserver(null, deliverSignal1);
        CountDownObserver observer2 = new CountDownObserver(null, deliverSignal2);
        CountDownObserver observer3 = new CountDownObserver(null, deliverSignal3);
        CountDownObserver observer4 = new CountDownObserver(null, deliverSignal4);
        CountDownObserver observer5 = new CountDownObserver(null, deliverSignal5);
        CountDownObserver observer6 = new CountDownObserver(null, deliverSignal6);
        CountDownObserver observer7 = new CountDownObserver(null, deliverSignal7);
        CountDownObserver observer8 = new CountDownObserver(null, deliverSignal8);
        CountDownObserver observer9 = new CountDownObserver(null, deliverSignal9);
        CountDownObserver observer10 = new CountDownObserver(null, deliverSignal10);
        CountDownObserver observer11 = new CountDownObserver(null, deliverSignal11);
        observers.add(observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer2);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                true,
                observer3);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                true,
                observer4);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(3)),
                true,
                observer5);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                true,
                observer6);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(3)),
                true,
                observer7);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer8);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4)),
                true,
                observer9);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4), new MovieId(1)),
                true,
                observer10);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4), new MovieId(3)),
                true,
                observer11);

        //noinspection ConstantConditions
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                        newValues,
                        null, null)).isEqualTo(2);

        deliverSignal1.await(5, TimeUnit.SECONDS);
        deliverSignal2.await(5, TimeUnit.SECONDS);
        deliverSignal3.await(5, TimeUnit.SECONDS);
        deliverSignal4.await(5, TimeUnit.SECONDS);
        deliverSignal5.await(5, TimeUnit.SECONDS);
        deliverSignal6.await(5, TimeUnit.SECONDS);
        deliverSignal7.await(5, TimeUnit.SECONDS);
        deliverSignal8.await(5, TimeUnit.SECONDS);
        deliverSignal9.await(100, TimeUnit.MILLISECONDS);
        deliverSignal10.await(100, TimeUnit.MILLISECONDS);
        deliverSignal11.await(100, TimeUnit.MILLISECONDS);
        assertThat(deliverSignal1.getCount()).isEqualTo(0);
        assertThat(deliverSignal2.getCount()).isEqualTo(0);
        assertThat(deliverSignal3.getCount()).isEqualTo(0);
        assertThat(deliverSignal4.getCount()).isEqualTo(0);
        assertThat(deliverSignal5.getCount()).isEqualTo(0);
        assertThat(deliverSignal6.getCount()).isEqualTo(0);
        assertThat(deliverSignal7.getCount()).isEqualTo(0);
        assertThat(deliverSignal8.getCount()).isEqualTo(0);
        // That's right, we do not expect to be notified on the updated row
        assertThat(deliverSignal9.getCount()).isEqualTo(1);
        assertThat(deliverSignal10.getCount()).isEqualTo(1);
        assertThat(deliverSignal11.getCount()).isEqualTo(1);
    }

    @Test
    public void updateByMovieId_getsNotifiedOnCertainUris() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);

        final CountDownLatch deliverSignal1 = new CountDownLatch(1);
        final CountDownLatch deliverSignal2 = new CountDownLatch(1);
        final CountDownLatch deliverSignal3 = new CountDownLatch(1);
        final CountDownLatch deliverSignal4 = new CountDownLatch(1);
        final CountDownLatch deliverSignal5 = new CountDownLatch(1);
        final CountDownLatch deliverSignal6 = new CountDownLatch(1);
        final CountDownLatch deliverSignal7 = new CountDownLatch(1);
        final CountDownLatch deliverSignal8 = new CountDownLatch(1);
        final CountDownLatch deliverSignal9 = new CountDownLatch(1);
        final CountDownLatch deliverSignal10 = new CountDownLatch(1);
        final CountDownLatch deliverSignal11 = new CountDownLatch(1);
        CountDownObserver observer1 = new CountDownObserver(null, deliverSignal1);
        CountDownObserver observer2 = new CountDownObserver(null, deliverSignal2);
        CountDownObserver observer3 = new CountDownObserver(null, deliverSignal3);
        CountDownObserver observer4 = new CountDownObserver(null, deliverSignal4);
        CountDownObserver observer5 = new CountDownObserver(null, deliverSignal5);
        CountDownObserver observer6 = new CountDownObserver(null, deliverSignal6);
        CountDownObserver observer7 = new CountDownObserver(null, deliverSignal7);
        CountDownObserver observer8 = new CountDownObserver(null, deliverSignal8);
        CountDownObserver observer9 = new CountDownObserver(null, deliverSignal9);
        CountDownObserver observer10 = new CountDownObserver(null, deliverSignal10);
        CountDownObserver observer11 = new CountDownObserver(null, deliverSignal11);
        observers.add(observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.CONTENT_URI,
                true,
                observer1);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                GenreEntity.CONTENT_URI,
                true,
                observer2);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2)),
                true,
                observer3);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                true,
                observer4);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4), new MovieId(1)),
                true,
                observer5);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                true,
                observer6);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4)),
                true,
                observer7);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieEntity.CONTENT_URI,
                true,
                observer8);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new MovieId(3)),
                true,
                observer9);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(3)),
                true,
                observer10);
        InstrumentationRegistry.getTargetContext().getContentResolver().registerContentObserver(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4), new MovieId(3)),
                true,
                observer11);

        //noinspection ConstantConditions
        assertThat(InstrumentationRegistry.getTargetContext().getContentResolver()
                .update(MovieGenreEntity.buildMovieGenreUri(new MovieId(1)),
                        newValues,
                        null, null)).isEqualTo(2);

        deliverSignal1.await(5, TimeUnit.SECONDS);
        deliverSignal2.await(5, TimeUnit.SECONDS);
        deliverSignal3.await(5, TimeUnit.SECONDS);
        deliverSignal4.await(5, TimeUnit.SECONDS);
        deliverSignal5.await(5, TimeUnit.SECONDS);
        deliverSignal6.await(5, TimeUnit.SECONDS);
        deliverSignal7.await(5, TimeUnit.SECONDS);
        deliverSignal8.await(5, TimeUnit.SECONDS);
        deliverSignal9.await(100, TimeUnit.MILLISECONDS);
        deliverSignal10.await(100, TimeUnit.MILLISECONDS);
        deliverSignal11.await(100, TimeUnit.MILLISECONDS);
        assertThat(deliverSignal1.getCount()).isEqualTo(0);
        assertThat(deliverSignal2.getCount()).isEqualTo(0);
        assertThat(deliverSignal3.getCount()).isEqualTo(0);
        assertThat(deliverSignal4.getCount()).isEqualTo(0);
        assertThat(deliverSignal5.getCount()).isEqualTo(0);
        assertThat(deliverSignal6.getCount()).isEqualTo(0);
        assertThat(deliverSignal7.getCount()).isEqualTo(0);
        assertThat(deliverSignal8.getCount()).isEqualTo(0);
        // That's right, we do not expect to be notified on the updated row
        assertThat(deliverSignal9.getCount()).isEqualTo(1);
        assertThat(deliverSignal10.getCount()).isEqualTo(1);
        assertThat(deliverSignal11.getCount()).isEqualTo(1);
    }

    // Not doing the same byGenre and byMovie
    @Test
    public void update_notifiesQueryCursorOnlyIfSetNotificationUri() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        InstrumentationRegistry.getTargetContext().getContentResolver().bulkInsert(
                MovieGenreEntity.CONTENT_URI,
                new ContentValues[]{value1, value2});

        //noinspection ConstantConditions
        cursor = InstrumentationRegistry.getTargetContext().getContentResolver().query(
                MovieGenreEntity.CONTENT_URI, null, null, null, null);

        final CountDownLatch deliverSignal = new CountDownLatch(1);
        CountDownObserver observer = new CountDownObserver(null, deliverSignal);
        observers.add(observer);
        cursor.registerContentObserver(observer);

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().update(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(2), new MovieId(1)),
                newValues, null, null);

        assertThat(deliverSignal.getCount()).isEqualTo(1);
        cursor.setNotificationUri(
                InstrumentationRegistry.getTargetContext().getContentResolver(),
                MovieGenreEntity.CONTENT_URI);

        //noinspection ConstantConditions
        InstrumentationRegistry.getTargetContext().getContentResolver().update(
                MovieGenreEntity.buildMovieGenreUri(new GenreId(4), new MovieId(1)),
                newValues, null, null);

        deliverSignal.await(5, TimeUnit.SECONDS);
        assertThat(deliverSignal.getCount()).isEqualTo(0);
    }

    private static class CountDownObserver extends ContentObserver
    {
        @NonNull private final CountDownLatch latch;
        public Uri latestChanged;
        @NonNull  public final Set<Uri> changedSet;

        public CountDownObserver(
                @Nullable Handler handler,
                @NonNull CountDownLatch latch)
        {
            super(handler);
            this.latch = latch;
            this.changedSet = new HashSet<>();
        }

        @Override public void onChange(boolean selfChange, Uri uri)
        {
            super.onChange(selfChange, uri);
            latestChanged = uri;
            changedSet.add(uri);
            latch.countDown();
        }
    }
}
