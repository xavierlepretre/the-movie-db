package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.EntitySQLiteOpenHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class MovieProviderDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.movie.db";
    private MovieProviderDelegate providerDelegate;
    private EntitySQLiteOpenHelper sqlHelper;

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = spy(new MovieProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/movie"),
                Uri.parse("content://content_authority/collection"),
                "dir_type",
                "item_type"));
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new MovieSQLHelperDelegate());
    }

    @After
    public void tearDown() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
    }

    @NonNull private ContentValues getMovie12()
    {
        ContentValues values = new ContentValues(21);
        values.put(MovieContract.COLUMN_ADULT, true);
        values.put(MovieContract.COLUMN_BACKDROP_PATH, "/backdrop1.jpg");
        values.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 33L);
        values.put(MovieContract.COLUMN_BUDGET, 100000L);
        values.put(MovieContract.COLUMN_HOMEPAGE, "homepage1");
        values.put(MovieContract._ID, 12L);
        values.put(MovieContract.COLUMN_IMDB_ID, "imdb1");
        values.put(MovieContract.COLUMN_ORIGINAL_LANGUAGE, "en");
        values.put(MovieContract.COLUMN_ORIGINAL_TITLE, "original_title1");
        values.put(MovieContract.COLUMN_OVERVIEW, "overview1");
        values.put(MovieContract.COLUMN_POPULARITY, 2.3F);
        values.put(MovieContract.COLUMN_POSTER_PATH, "/poster1.jpg");
        values.put(MovieContract.COLUMN_RELEASE_DATE, "2011-11-03");
        values.put(MovieContract.COLUMN_REVENUE, 200000L);
        values.put(MovieContract.COLUMN_RUNTIME, 120);
        values.put(MovieContract.COLUMN_STATUS, "released");
        values.put(MovieContract.COLUMN_TAGLINE, "tagline1");
        values.put(MovieContract.COLUMN_TITLE, "title1");
        values.put(MovieContract.COLUMN_VIDEO, true);
        values.put(MovieContract.COLUMN_VOTE_AVERAGE, 3.4F);
        values.put(MovieContract.COLUMN_VOTE_COUNT, 46);
        return values;
    }

    private void assertThatIsMovie12(@NonNull Cursor cursor) throws Exception
    {
        assertThat(cursor.getInt(cursor.getColumnIndex(MovieContract.COLUMN_ADULT))).isEqualTo(1);
        assertThat(cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_BACKDROP_PATH))).isEqualTo("/backdrop1.jpg");
        assertThat(cursor.getLong(cursor.getColumnIndex(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID))).isEqualTo(33L);
        assertThat(cursor.getLong(cursor.getColumnIndex(MovieContract.COLUMN_BUDGET))).isEqualTo(100000L);
        assertThat(cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_HOMEPAGE))).isEqualTo("homepage1");
        assertThat(cursor.getLong(cursor.getColumnIndex(MovieContract._ID))).isEqualTo(12L);
        assertThat(cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_IMDB_ID))).isEqualTo("imdb1");
        assertThat(cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_ORIGINAL_LANGUAGE))).isEqualTo("en");
        assertThat(cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_ORIGINAL_TITLE))).isEqualTo("original_title1");
        assertThat(cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_OVERVIEW))).isEqualTo("overview1");
        assertThat(cursor.getFloat(cursor.getColumnIndex(MovieContract.COLUMN_POPULARITY))).isEqualTo(2.3F);
        assertThat(cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_POSTER_PATH))).isEqualTo("/poster1.jpg");
        assertThat(cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_RELEASE_DATE))).isEqualTo("2011-11-03");
        assertThat(cursor.getLong(cursor.getColumnIndex(MovieContract.COLUMN_REVENUE))).isEqualTo(200000L);
        assertThat(cursor.getInt(cursor.getColumnIndex(MovieContract.COLUMN_RUNTIME))).isEqualTo(120);
        assertThat(cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_STATUS))).isEqualTo("released");
        assertThat(cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_TAGLINE))).isEqualTo("tagline1");
        assertThat(cursor.getString(cursor.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("title1");
        assertThat(cursor.getInt(cursor.getColumnIndex(MovieContract.COLUMN_VIDEO))).isEqualTo(1);
        assertThat(cursor.getFloat(cursor.getColumnIndex(MovieContract.COLUMN_VOTE_AVERAGE))).isEqualTo(3.4F);
        assertThat(cursor.getInt(cursor.getColumnIndex(MovieContract.COLUMN_VOTE_COUNT))).isEqualTo(46);
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
        assertThat(matcher.match(Uri.parse("content://content_authority/movie"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/movie/a"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/movie/456"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/movie/456/t"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/collection/456/movie"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/collection/456/movie/1"))).isEqualTo(UriMatcher.NO_MATCH);
    }

    @Test
    public void returnsProperTypes() throws Exception
    {
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/movie"))).isEqualTo("dir_type");
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/movie/34"))).isEqualTo("item_type");
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/collection/34/movie"))).isEqualTo("dir_type");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_mayFail() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/fake"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void properTypesFailsOnUnknownPath() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/movie/a"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void properTypesFailsOnUnknownCollectionPath() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/collection/a/movie"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMovieIdFromPath_doesNotAcceptFake() throws Exception
    {
        providerDelegate.getMovieId(Uri.parse("content://anything/fake/2"));
    }

    @Test
    public void getMovieIdFromPath_isOk() throws Exception
    {
        assertThat(providerDelegate.getMovieId(Uri.parse("content://content_authority/movie/1"))).isEqualTo("1");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://content_authority/movie/a"))).isEqualTo("a");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://anything/movie/1"))).isEqualTo("1");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://anything/movie/a"))).isEqualTo("a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCollectionIdFromPath_doesNotAcceptFake() throws Exception
    {
        providerDelegate.getCollectionId(Uri.parse("content://anything/fake/2/movie"));
    }

    @Test
    public void getCollectionIdFromPath_isOk() throws Exception
    {
        assertThat(providerDelegate.getCollectionId(Uri.parse("content://content_authority/collection/1/movie"))).isEqualTo("1");
        assertThat(providerDelegate.getCollectionId(Uri.parse("content://content_authority/collection/a/movie"))).isEqualTo("a");
        assertThat(providerDelegate.getCollectionId(Uri.parse("content://anything/collection/1/movie"))).isEqualTo("1");
        assertThat(providerDelegate.getCollectionId(Uri.parse("content://anything/collection/a/movie"))).isEqualTo("a");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getMovieIdFromPath_canFail() throws Exception
    {
        providerDelegate.getMovieId(Uri.parse("content://content_authority/movie"));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getCollectionIdFromPath_canFail() throws Exception
    {
        providerDelegate.getCollectionId(Uri.parse("content://content_authority/collection"));
    }

    @Test
    public void buildMovieLocation_isOk() throws Exception
    {
        assertThat(MovieProviderDelegate.buildMovieLocation(
                Uri.parse("content://something"), new MovieId(60)))
                .isEqualTo(Uri.parse("content://something/60"));
        assertThat(providerDelegate.buildMovieLocation(new MovieId(870)))
                .isEqualTo(Uri.parse("content://content_authority/movie/870"));
    }

    @Test
    public void buildCollectionMoviesLocation_isOk() throws Exception
    {
        assertThat(MovieProviderDelegate.buildCollectionMoviesLocation(
                Uri.parse("content://something"), new CollectionId(60)))
                .isEqualTo(Uri.parse("content://something/60/movie"));
        assertThat(providerDelegate.buildCollectionMoviesLocation(new CollectionId(870)))
                .isEqualTo(Uri.parse("content://content_authority/collection/870/movie"));
    }

    @Test(expected = SQLiteException.class)
    public void insertNull_fails() throws Exception
    {
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withMissingId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract.COLUMN_TITLE, "Skyfall");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withNullId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, (Integer) null);
        values.put(MovieContract.COLUMN_TITLE, "Skyfall");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);
    }

    @Test
    public void insertPutsInDb_andCanQuery() throws Exception
    {
        ContentValues values = getMovie12();
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/movie/12");

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThatIsMovie12(myMovie);
    }

    @Test
    public void insertTwiceTheSame_replaces() throws Exception
    {
        ContentValues values = getMovie12();
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/movie/12");

        values.put(MovieContract.COLUMN_TITLE, "title2");
        //noinspection ConstantConditions
        Uri replaced = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);
        //noinspection ConstantConditions
        assertThat(replaced.toString()).isEqualTo("content://content_authority/movie/12");

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getString(myMovie.getColumnIndex(MovieContract.COLUMN_TITLE)))
                .isEqualTo("title2");
    }

    @Test
    public void canInsertWithNullFields() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieContract._ID, 12);

        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/movie/12");

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_ADULT)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_BACKDROP_PATH)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_BUDGET)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_HOMEPAGE)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract._ID)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_IMDB_ID)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_ORIGINAL_LANGUAGE)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_ORIGINAL_TITLE)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_OVERVIEW)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_POPULARITY)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_POSTER_PATH)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_RELEASE_DATE)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_REVENUE)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_RUNTIME)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_STATUS)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_TAGLINE)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_TITLE)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_VIDEO)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_VOTE_AVERAGE)));
        assertThat(myMovie.isNull(myMovie.getColumnIndex(MovieContract.COLUMN_VOTE_COUNT)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void insertOnMovieById_fails() throws Exception
    {
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                getMovie12());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void insertOnCollectionMovies_fails() throws Exception
    {
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/33/movie"),
                getMovie12());
    }

    @Test
    public void bulkInsert_with1Null_skips() throws Exception
    {
        ContentValues value2 = new ContentValues();
        value2.put(MovieContract._ID, 12);
        ContentValues[] values = new ContentValues[]{null, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values))
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withMissingId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieContract.COLUMN_TITLE, "title1");
        ContentValues value2 = new ContentValues();
        value2.put(MovieContract._ID, 4);
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values))
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withNullId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieContract._ID, (Integer) null);
        value1.put(MovieContract.COLUMN_TITLE, "title1");
        ContentValues value2 = new ContentValues();
        value2.put(MovieContract._ID, 4);
        value2.put(MovieContract.COLUMN_TITLE, "title2");
        ContentValues[] values = new ContentValues[]{value1, value2};

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values))
                .isEqualTo(1);
    }

    @Test
    public void bulkInsertInDb_andCanQuery() throws Exception
    {
        ContentValues value1 = getMovie12();
        ContentValues value2 = new ContentValues();
        value2.put(MovieContract._ID, 13);
        value2.put(MovieContract.COLUMN_TITLE, "title2");
        ContentValues[] values = new ContentValues[]{value1, value2};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values))
                .isEqualTo(2);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThatIsMovie12(myMovie);
        assertThat(myMovie.moveToNext()).isTrue();
        assertThat(myMovie.getString(myMovie.getColumnIndex(MovieContract.COLUMN_TITLE)))
                .isEqualTo("title2");
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertEmptyInDb() throws Exception
    {
        ContentValues[] values = new ContentValues[0];
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values))
                .isEqualTo(0);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertExisting_overwrites() throws Exception
    {
        ContentValues value1 = getMovie12();
        assertThat(providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                value1))
                .isEqualTo(Uri.parse("content://content_authority/movie/12"));

        value1.put(MovieContract.COLUMN_TITLE, "title2");
        ContentValues value2 = new ContentValues();
        value2.put(MovieContract._ID, 13);
        value2.put(MovieContract.COLUMN_TITLE, "title3");
        ContentValues[] values = new ContentValues[]{value1, value2};
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values))
                .isEqualTo(2);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getString(myMovie.getColumnIndex(MovieContract.COLUMN_TITLE)))
                .isEqualTo("title2");
        assertThat(myMovie.moveToNext()).isTrue();
        assertThat(myMovie.getString(myMovie.getColumnIndex(MovieContract.COLUMN_TITLE)))
                .isEqualTo("title3");
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertDuplicate_lastWins() throws Exception
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
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values))
                .isEqualTo(3);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieContract._ID)))
                .isEqualTo(3);
        assertThat(myMovie.getString(myMovie.getColumnIndex(MovieContract.COLUMN_TITLE)))
                .isEqualTo("title2");
        assertThat(myMovie.moveToNext()).isTrue();
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieContract._ID)))
                .isEqualTo(4);
        assertThat(myMovie.getString(myMovie.getColumnIndex(MovieContract.COLUMN_TITLE)))
                .isEqualTo("title3");
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void bulkInsertOnMovieById_fails() throws Exception
    {
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                new ContentValues[]{getMovie12()});
    }

    @Test(expected = UnsupportedOperationException.class)
    public void bulkInsertOnCollectionMovies_fails() throws Exception
    {
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/collection/12/movie"),
                new ContentValues[]{getMovie12()});
    }

    @Test
    public void queryListFromDb_isOk() throws Exception
    {
        ContentValues values1 = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(MovieContract._ID, 13);
        values2.put(MovieContract.COLUMN_TITLE, "title2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie"),
                null, null, null, null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        assertThatIsMovie12(found);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID)))
                .isEqualTo(13);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE)))
                .isEqualTo("title2");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryListFromDbWithSelection_isOk() throws Exception
    {
        ContentValues values1 = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(MovieContract._ID, 13);
        values2.put(MovieContract.COLUMN_TITLE, "title2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie"),
                null,
                MovieContract.COLUMN_TITLE + "=?",
                new String[]{"title1"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThatIsMovie12(found);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithSelection_isOk() throws Exception
    {
        ContentValues values = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                null,
                MovieContract.COLUMN_TITLE + "=?",
                new String[]{"title1"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThatIsMovie12(found);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithContradictorySelection_returnsEmpty() throws Exception
    {
        ContentValues values = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                null,
                MovieContract.COLUMN_TITLE + "=?",
                new String[]{"title2"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);
    }

    @Test
    public void queryMoviesInCollection_isOk() throws Exception
    {
        ContentValues values1 = getMovie12();
        ContentValues values2 = new ContentValues();
        values2.put(MovieContract._ID, 13);
        values2.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 33L);
        values2.put(MovieContract.COLUMN_TITLE, "title2");
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                new ContentValues[]{values1, values2});

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/33/movie"),
                null, null, null, null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        assertThatIsMovie12(found);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID)))
                .isEqualTo(13);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE)))
                .isEqualTo("title2");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryMoviesInCollectionWithSelection_isOk() throws Exception
    {
        ContentValues values1 = getMovie12();
        ContentValues values2 = new ContentValues();
        values2.put(MovieContract._ID, 13);
        values2.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, 33L);
        values2.put(MovieContract.COLUMN_TITLE, "title2");
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                new ContentValues[]{values1, values2});

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/33/movie"),
                null,
                MovieContract.COLUMN_TITLE + "=?",
                new String[]{"title1"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThatIsMovie12(found);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryMoviesInCollectionWithContradictorySelection_returnsEmpty() throws Exception
    {
        ContentValues values1 = getMovie12();
        ContentValues values2 = new ContentValues();
        values2.put(MovieContract._ID, 13);
        values2.put(MovieContract.COLUMN_TITLE, "title2");
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                new ContentValues[]{values1, values2});

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/collection/33/movie"),
                null,
                MovieContract.COLUMN_TITLE + "=?",
                new String[]{"title2"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);
    }

    @Test
    public void deleteItem_isOk() throws Exception
    {
        ContentValues values = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                null, null)).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie"),
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
        ContentValues values = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                MovieContract.COLUMN_TITLE + "=?",
                new String[]{"title1"})).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie"),
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
        ContentValues values = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                MovieContract.COLUMN_TITLE + "=?",
                new String[]{"title2"})).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
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
        ContentValues values1 = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(MovieContract._ID, 13);
        values2.put(MovieContract.COLUMN_TITLE, "title2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values2);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                MovieContract.COLUMN_TITLE + "=?",
                new String[]{"title2"})).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie"),
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
        ContentValues values1 = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values1);
        ContentValues values2 = new ContentValues();
        values2.put(MovieContract._ID, 13);
        values2.put(MovieContract.COLUMN_TITLE, "title2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values2);
        ContentValues values3 = new ContentValues();
        values3.put(MovieContract._ID, 14);
        values3.put(MovieContract.COLUMN_TITLE, "James Bond");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values3);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                MovieContract.COLUMN_TITLE + " LIKE ?",
                new String[]{"title%"})).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie"),
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
        ContentValues values = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(MovieContract.COLUMN_TITLE, "title2");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                newValues,
                null, null)).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID))).isEqualTo(12);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("title2");
    }

    @Test
    public void updateItemWithSelection_isOk() throws Exception
    {
        ContentValues values = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(MovieContract.COLUMN_TITLE, "title2");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                newValues,
                MovieContract.COLUMN_TITLE + "=?",
                new String[]{"title1"})).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID))).isEqualTo(12);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("title2");
    }

    @Test
    public void updateItemWithChangedId_isOk() throws Exception
    {
        ContentValues values = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(MovieContract._ID, 13);
        newValues.put(MovieContract.COLUMN_TITLE, "title2");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                newValues,
                MovieContract.COLUMN_TITLE + "=?",
                new String[]{"title1"})).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/13"),
                null,
                null,
                null,
                null, null, null, null);
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID))).isEqualTo(13);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("title2");
    }

    @Test
    public void updateItemWithContradictorySelection_doesNotUpdate() throws Exception
    {
        ContentValues values = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(MovieContract.COLUMN_TITLE, "Comic");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                newValues,
                MovieContract.COLUMN_TITLE + "=?",
                new String[]{"title2"})).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThatIsMovie12(found);
    }

    @Test
    public void updateItemWithMissingElement_doesNotLoseSavedInfo() throws Exception
    {
        ContentValues values = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);

        ContentValues newValues = new ContentValues();
        newValues.put(MovieContract.COLUMN_TITLE, "title2");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                newValues,
                null, null)).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID))).isEqualTo(12);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("title2");
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_POSTER_PATH))).isEqualTo("/poster1.jpg");
    }

    @Test
    public void updateList_isOk() throws Exception
    {
        ContentValues values = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);
        ContentValues values2 = new ContentValues();
        values2.put(MovieContract._ID, 13);
        values2.put(MovieContract.COLUMN_TITLE, "title2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values2);

        ContentValues newValues = new ContentValues();
        newValues.put(MovieContract.COLUMN_TITLE, "title3");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                newValues,
                null,
                null)).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID))).isEqualTo(12);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("title3");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/13"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID))).isEqualTo(13);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("title3");
    }

    @Test
    public void updateListWithSelection_isOk() throws Exception
    {
        ContentValues values = getMovie12();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values);
        ContentValues values2 = new ContentValues();
        values2.put(MovieContract._ID, 13);
        values2.put(MovieContract.COLUMN_TITLE, "title2");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values2);
        ContentValues values3 = new ContentValues();
        values3.put(MovieContract._ID, 14);
        values3.put(MovieContract.COLUMN_TITLE, "title3");
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                values3);

        ContentValues newValues = new ContentValues();
        newValues.put(MovieContract.COLUMN_TITLE, "title4");
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                newValues,
                MovieContract.TABLE_NAME + "." + MovieContract._ID + "<=?",
                new String[]{"13"})).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/12"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID))).isEqualTo(12);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("title4");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/13"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID))).isEqualTo(13);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("title4");

        found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/14"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID))).isEqualTo(14);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("title3");
    }
}