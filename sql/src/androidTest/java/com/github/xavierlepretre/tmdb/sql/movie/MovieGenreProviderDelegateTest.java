package com.github.xavierlepretre.tmdb.sql.movie;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.sql.EntitySQLiteOpenHelper;
import com.github.xavierlepretre.tmdb.model.movie.GenreContract;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;
import com.github.xavierlepretre.tmdb.model.movie.MovieContract;
import com.github.xavierlepretre.tmdb.model.movie.MovieGenreContract;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MovieGenreProviderDelegateTest
{
    private static final String TEMP_DB_NAME = "temp.movieGenre.db";
    private MovieGenreProviderDelegate providerDelegate;
    private MovieProviderDelegate movieProviderDelegate;
    private GenreProviderDelegate genreProviderDelegate;
    private EntitySQLiteOpenHelper sqlHelper;

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = new MovieGenreProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/movieGenre"),
                "movieGenre_dir_type",
                "movieGenre_item_type",
                Uri.parse("content://content_authority/genre"),
                "genre_dir_type",
                Uri.parse("content://content_authority/movie"),
                "movie_dir_type");
        movieProviderDelegate = new MovieProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/movie"),
                Uri.parse("content://content_authority/collection"),
                "movie_dir_type",
                "movie_item_type");
        genreProviderDelegate = new GenreProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/genre"),
                "genre_dir_type",
                "genre_item_type");
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new MovieSQLHelperDelegate(),
                new GenreSQLHelperDelegate(),
                new MovieGenreSQLHelperDelegate());
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
        assertThat(matcher.match(Uri.parse("content://content_authority/movie"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/movieGenre"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/movie/a"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/movie/456"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/movie/456/t"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/movie/456/genre"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/movie/456/genre/a"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/movie/456/genre/3"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/genre"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/genre/456"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/genre/456/movie"))).isEqualTo(number);
        assertThat(matcher.match(Uri.parse("content://content_authority/genre/456/movie/a"))).isEqualTo(UriMatcher.NO_MATCH);
        assertThat(matcher.match(Uri.parse("content://content_authority/genre/456/movie/1"))).isEqualTo(number);
    }

    @Test
    public void returnsProperTypes() throws Exception
    {
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/movieGenre"))).isEqualTo("movieGenre_dir_type");
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/movie/34/genre"))).isEqualTo("genre_dir_type");
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/genre/34/movie"))).isEqualTo("movie_dir_type");
        assertThat(providerDelegate.getType(Uri.parse("content://content_authority/genre/34/movie/3"))).isEqualTo("movieGenre_item_type");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getType_mayFail() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/movie"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void properTypesFailsOnUnknownPath() throws Exception
    {
        providerDelegate.getType(Uri.parse("content://content_authority/movie/34"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMovieIdFromPath_doesNotAcceptFake() throws Exception
    {
        providerDelegate.getMovieId(Uri.parse("content://anything/fake/2/movie"));
    }

    @Test
    public void getMovieIdFromPath_isOk() throws Exception
    {
        assertThat(providerDelegate.getMovieId(Uri.parse("content://content_authority/movie/1"))).isEqualTo("1");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://content_authority/movie/1/genre"))).isEqualTo("1");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://content_authority/movie/1/genre/2"))).isEqualTo("1");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://content_authority/movie/a"))).isEqualTo("a");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://content_authority/movie/a/genre"))).isEqualTo("a");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://content_authority/movie/a/genre/b"))).isEqualTo("a");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://anything/movie/1"))).isEqualTo("1");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://anything/movie/1/genre"))).isEqualTo("1");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://anything/movie/1/genre/2"))).isEqualTo("1");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://anything/movie/a"))).isEqualTo("a");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://anything/movie/a/genre"))).isEqualTo("a");
        assertThat(providerDelegate.getMovieId(Uri.parse("content://anything/movie/a/genre/b"))).isEqualTo("a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMovieIdFromPath_failsIfNotMovie() throws Exception
    {
        providerDelegate.getMovieId(Uri.parse("content://anything/notMovie/1/genre/1"));
    }

    @Test
    public void getGenreIdFromPath_isOk() throws Exception
    {
        assertThat(providerDelegate.getGenreId(Uri.parse("content://content_authority/genre/1"))).isEqualTo("1");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://content_authority/genre/1/movie"))).isEqualTo("1");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://content_authority/genre/1/movie/2"))).isEqualTo("1");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://content_authority/genre/a"))).isEqualTo("a");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://content_authority/genre/a/movie"))).isEqualTo("a");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://content_authority/genre/a/movie/b"))).isEqualTo("a");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://anything/genre/1"))).isEqualTo("1");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://anything/genre/1/movie"))).isEqualTo("1");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://anything/genre/1/movie/2"))).isEqualTo("1");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://anything/genre/a"))).isEqualTo("a");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://anything/genre/a/movie"))).isEqualTo("a");
        assertThat(providerDelegate.getGenreId(Uri.parse("content://anything/genre/a/movie/b"))).isEqualTo("a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGenreIdFromPath_failsIfNotGenre() throws Exception
    {
        providerDelegate.getMovieId(Uri.parse("content://anything/notGenre/1/movie/1"));
    }

    @Test
    public void buildMovieLocation_isOk() throws Exception
    {
        assertThat(MovieGenreProviderDelegate.buildMovieGenreLocation(
                Uri.parse("content://something/genre"),
                new GenreId(1),
                new MovieId(60)))
                .isEqualTo(Uri.parse("content://something/genre/1/movie/60"));
        assertThat(providerDelegate.buildMovieGenreLocation(
                new GenreId(1), new MovieId(870)))
                .isEqualTo(Uri.parse("content://content_authority/genre/1/movie/870"));

        assertThat(MovieGenreProviderDelegate.buildMovieGenreLocation(
                Uri.parse("content://something/movie"),
                new MovieId(60)))
                .isEqualTo(Uri.parse("content://something/movie/60/genre"));
        assertThat(providerDelegate.buildMovieGenreLocation(
                new MovieId(870)))
                .isEqualTo(Uri.parse("content://content_authority/movie/870/genre"));

        assertThat(MovieGenreProviderDelegate.buildMovieGenreLocation(
                Uri.parse("content://something/genre"),
                new GenreId(60)))
                .isEqualTo(Uri.parse("content://something/genre/60/movie"));
        assertThat(providerDelegate.buildMovieGenreLocation(
                new GenreId(870)))
                .isEqualTo(Uri.parse("content://content_authority/genre/870/movie"));
    }

    @Test(expected = SQLiteException.class)
    public void insertNull_fails() throws Exception
    {
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null);
    }

    @Test(expected = SQLiteException.class)
    public void insertNullForGenre_fails() throws Exception
    {
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/1/movie"),
                null);
    }

    @Test(expected = SQLiteException.class)
    public void insertNullForMovie_fails() throws Exception
    {
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                null);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withMissingId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                values);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertForGenre_withMissingId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/1/movie"),
                values);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertForMovie_withMissingId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                values);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insert_withNullId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieGenreContract.COLUMN_GENRE_ID, (Integer) null);
        values.put(MovieGenreContract.COLUMN_MOVIE_ID, (Long) null);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                values);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertForGenre_withNullId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieGenreContract.COLUMN_MOVIE_ID, (Long) null);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/1/movie"),
                values);
    }

    @Test(expected = SQLiteConstraintException.class)
    public void insertForMovie_withNullId_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieGenreContract.COLUMN_GENRE_ID, (Integer) null);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                values);
    }

    @Test
    public void insertPutsInDb_andCanQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        values.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/genre/2/movie/1");

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void insertForGenrePutsInDb_andCanQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/genre/2/movie/1");

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void insertForMoviePutsInDb_andCanQuery() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/genre/2/movie/1");

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                inserted, null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void insertTwiceTheSame_replaces() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        values.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/genre/2/movie/1");

        //noinspection ConstantConditions
        Uri replaced = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(replaced.toString()).isEqualTo("content://content_authority/genre/2/movie/1");

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID)))
                .isEqualTo(2);
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID)))
                .isEqualTo(1);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void insertForGenreTwiceTheSame_replaces() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/genre/2/movie/1");

        //noinspection ConstantConditions
        Uri replaced = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(replaced.toString()).isEqualTo("content://content_authority/genre/2/movie/1");

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID)))
                .isEqualTo(2);
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID)))
                .isEqualTo(1);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void insertForMovieTwiceTheSame_replaces() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        Uri inserted = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(inserted.toString()).isEqualTo("content://content_authority/genre/2/movie/1");

        //noinspection ConstantConditions
        Uri replaced = providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                values).getInserted();
        //noinspection ConstantConditions
        assertThat(replaced.toString()).isEqualTo("content://content_authority/genre/2/movie/1");

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID)))
                .isEqualTo(2);
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID)))
                .isEqualTo(1);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void insertOnMovieGenreById_fails() throws Exception
    {
        ContentValues values = new ContentValues();
        values.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        values.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/1"),
                values);
    }

    @Test
    public void bulkInsert_with1Null_skips() throws Exception
    {
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{null, value2}).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsertForGenre_with1Null_skips() throws Exception
    {
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                new ContentValues[]{null, value2}).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsertForMovie_with1Null_skips() throws Exception
    {
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                new ContentValues[]{null, value2}).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withMissingIds_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withMissingGenreId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withMissingMovieId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsertForGenre_withMissingMovieId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsertForMovie_withMissingGenreId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsert_withNullIds_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, (Long) null);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, (Integer) null);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsertForGenre_withNullMovieId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, (Long) null);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsertForMovie_withNullGenreId_skips() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, (Integer) null);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);

        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(1);
    }

    @Test
    public void bulkInsertInDb_andCanQuery() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(2);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(3L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertForGenreInDb_andCanQuery() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(2);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(3L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertForMovieInDb_andCanQuery() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(2);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertEmptyInDb() throws Exception
    {
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[0]).getCount())
                .isEqualTo(0);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertForGenreEmptyInDb() throws Exception
    {
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/1/movie"),
                new ContentValues[0]).getCount())
                .isEqualTo(0);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertForMovieEmptyInDb() throws Exception
    {
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                new ContentValues[0]).getCount())
                .isEqualTo(0);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertExisting_overwrites() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        assertThat(providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                value1).getInserted())
                .isEqualTo(Uri.parse("content://content_authority/genre/2/movie/1"));

        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(2);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(3L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertForGenreExisting_overwrites() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        assertThat(providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                value1).getInserted())
                .isEqualTo(Uri.parse("content://content_authority/genre/2/movie/1"));

        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(2);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(3L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertForMovieExisting_overwrites() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        assertThat(providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                value1).getInserted())
                .isEqualTo(Uri.parse("content://content_authority/genre/2/movie/1"));

        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                new ContentValues[]{value1, value2}).getCount())
                .isEqualTo(2);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertDuplicate_lastWins() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3}).getCount())
                .isEqualTo(3);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(3);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertForGenreDuplicate_lastWins() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                new ContentValues[]{value1, value2, value3}).getCount())
                .isEqualTo(3);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(2L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test
    public void bulkInsertForMovieDuplicate_lastWins() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        assertThat(providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                new ContentValues[]{value1, value2, value3}).getCount())
                .isEqualTo(3);

        Cursor myMovie = providerDelegate.query(sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(myMovie).isNotNull();
        //noinspection ConstantConditions
        assertThat(myMovie.moveToFirst()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(myMovie.moveToNext()).isTrue();
        assertThat(myMovie.getLong(myMovie.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(myMovie.getInt(myMovie.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(3);
        assertThat(myMovie.moveToNext()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void bulkInsertOnId_fails() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/1"),
                new ContentValues[]{value1});
    }

    @Test
    public void queryList_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2});

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null, null, null, null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(3);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryListWithSelection_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2});

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                MovieGenreContract.COLUMN_GENRE_ID + "=?",
                new String[]{"2"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryListForGenre_isOk() throws Exception
    {
        ContentValues movie1 = new ContentValues();
        movie1.put(MovieContract._ID, 1);
        movie1.put(MovieContract.COLUMN_TITLE, "movie1");
        ContentValues movie2 = new ContentValues();
        movie2.put(MovieContract._ID, 2);
        movie2.put(MovieContract.COLUMN_TITLE, "movie2");
        movieProviderDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                new ContentValues[]{movie1, movie2});

        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                null, null, null, null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID))).isEqualTo(1L);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("movie1");
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID))).isEqualTo(2L);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("movie2");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryListForGenreWithSelection_isOk() throws Exception
    {
        ContentValues movie1 = new ContentValues();
        movie1.put(MovieContract._ID, 1);
        movie1.put(MovieContract.COLUMN_TITLE, "movie1");
        ContentValues movie2 = new ContentValues();
        movie2.put(MovieContract._ID, 2);
        movie2.put(MovieContract.COLUMN_TITLE, "movie2");
        movieProviderDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie"),
                new ContentValues[]{movie1, movie2});

        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                null,
                MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_MOVIE_ID + "=?",
                new String[]{"2"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieContract._ID))).isEqualTo(2L);
        assertThat(found.getString(found.getColumnIndex(MovieContract.COLUMN_TITLE))).isEqualTo("movie2");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryListForMovie_isOk() throws Exception
    {
        ContentValues genre1 = new ContentValues();
        genre1.put(GenreContract._ID, 2);
        genre1.put(GenreContract.COLUMN_NAME, "genre1");
        ContentValues genre2 = new ContentValues();
        genre2.put(GenreContract._ID, 3);
        genre2.put(GenreContract.COLUMN_NAME, "genre2");
        genreProviderDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                new ContentValues[]{genre1, genre2});

        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                null, null, null, null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getInt(found.getColumnIndex(GenreContract._ID))).isEqualTo(2);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME))).isEqualTo("genre1");
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getInt(found.getColumnIndex(GenreContract._ID))).isEqualTo(3);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME))).isEqualTo("genre2");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryListForMovieWithSelection_isOk() throws Exception
    {
        ContentValues genre1 = new ContentValues();
        genre1.put(GenreContract._ID, 2);
        genre1.put(GenreContract.COLUMN_NAME, "genre1");
        ContentValues genre2 = new ContentValues();
        genre2.put(GenreContract._ID, 3);
        genre2.put(GenreContract.COLUMN_NAME, "genre2");
        genreProviderDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre"),
                new ContentValues[]{genre1, genre2});

        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                null,
                MovieGenreContract.TABLE_NAME + "." + MovieGenreContract.COLUMN_GENRE_ID + ">=?",
                new String[]{"3"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getInt(found.getColumnIndex(GenreContract._ID))).isEqualTo(3);
        assertThat(found.getString(found.getColumnIndex(GenreContract.COLUMN_NAME))).isEqualTo("genre2");
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemWithSelection_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                value1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/1"),
                null,
                MovieGenreContract.COLUMN_MOVIE_ID + "=?",
                new String[]{"1"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void queryItemFromDbWithContradictorySelection_returnsEmpty() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                value1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/1"),
                null,
                MovieGenreContract.COLUMN_MOVIE_ID + "=?",
                new String[]{"3"},
                null, null, null, null);

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);
    }

    @Test
    public void deleteItem_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                value1);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/1"),
                null, null).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
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
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                value1);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/1"),
                MovieGenreContract.COLUMN_MOVIE_ID + "=?",
                new String[]{"1"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
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
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                value1);

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/1"),
                MovieGenreContract.COLUMN_MOVIE_ID + "=?",
                new String[]{"3"}).getCount()).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
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
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2});

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/1"),
                MovieGenreContract.COLUMN_GENRE_ID + "=?",
                new String[]{"3"}).getCount()).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
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
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null).getCount()).isEqualTo(3);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null,
                null,
                null, null, null, null);
        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(0);
    }

    @Test
    public void deleteListWithSelection_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                MovieGenreContract.COLUMN_MOVIE_ID + "<=?",
                new String[]{"1"}).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null,
                null,
                null, null, null, null);
        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
    }

    @Test
    public void deleteListForGenre_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                null,
                null).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null,
                null,
                null, null, null, null);
        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(3);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void deleteListForGenreWithSelection_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                MovieGenreContract.COLUMN_MOVIE_ID + "<=?",
                new String[]{"1"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null,
                null,
                null, null, null, null);
        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(3);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(2L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void deleteListForMovie_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                null,
                null).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null,
                null,
                null, null, null, null);
        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(2L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void deleteListForMovieWithSelection_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        assertThat(providerDelegate.delete(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                MovieGenreContract.COLUMN_GENRE_ID + "<=?",
                new String[]{"2"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null,
                null,
                null, null, null, null);
        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(2);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(3);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(2L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void updateItem_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                value1);

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/1"),
                newValues,
                null, null).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/2"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(2L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
    }

    @Test
    public void updateItemWithSelection_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                value1);

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/1"),
                newValues,
                MovieGenreContract.COLUMN_MOVIE_ID + "<=?",
                new String[]{"1"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/2"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(2L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
    }

    @Test
    public void updateItemWithContradictorySelection_doesNotUpdate() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                value1);

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/1"),
                newValues,
                MovieGenreContract.COLUMN_MOVIE_ID + "=?",
                new String[]{"2"}).getCount()).isEqualTo(0);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie/1"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
    }

    @Test
    public void updateList_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                newValues,
                null,
                null).getCount()).isEqualTo(3);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(3);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(2L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(3L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void updateListWillConflict_fails() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                newValues,
                null,
                null);
    }

    @Test
    public void updateListWithSelection_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                newValues,
                MovieGenreContract.COLUMN_GENRE_ID + "=?",
                new String[]{"2"}).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(3);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(3);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(2L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void updateListForGenre_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                newValues,
                null,
                null).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(3);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(2L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(3);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(3L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void updateListForGenreWillConflict_fails() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                newValues,
                null,
                null);
    }

    @Test
    public void updateListForGenreWithSelection_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/genre/2/movie"),
                newValues,
                MovieGenreContract.COLUMN_GENRE_ID + "=?",
                new String[]{"2"}).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(3);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(3);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(2L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test
    public void updateListForMovie_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_MOVIE_ID, 3);
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                newValues,
                null,
                null).getCount()).isEqualTo(2);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(3);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(3L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(3L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(3);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(2L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(found.moveToNext()).isFalse();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void updateListForMovieWillConflict_fails() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                newValues,
                null,
                null);
    }

    @Test
    public void updateListForMovieWithSelection_isOk() throws Exception
    {
        ContentValues value1 = new ContentValues();
        value1.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value1.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        ContentValues value2 = new ContentValues();
        value2.put(MovieGenreContract.COLUMN_MOVIE_ID, 1);
        value2.put(MovieGenreContract.COLUMN_GENRE_ID, 3);
        ContentValues value3 = new ContentValues();
        value3.put(MovieGenreContract.COLUMN_MOVIE_ID, 2);
        value3.put(MovieGenreContract.COLUMN_GENRE_ID, 2);
        providerDelegate.bulkInsert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                new ContentValues[]{value1, value2, value3});

        ContentValues newValues = new ContentValues();
        newValues.put(MovieGenreContract.COLUMN_GENRE_ID, 4);
        assertThat(providerDelegate.update(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/movie/1/genre"),
                newValues,
                MovieGenreContract.COLUMN_GENRE_ID + "=?",
                new String[]{"2"}).getCount()).isEqualTo(1);

        Cursor found = providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/movieGenre"),
                null,
                null,
                null,
                null, null, null, null);

        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(3);
        assertThat(found.moveToFirst()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(4);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(1L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(3);
        assertThat(found.moveToNext()).isTrue();
        assertThat(found.getLong(found.getColumnIndex(MovieGenreContract.COLUMN_MOVIE_ID))).isEqualTo(2L);
        assertThat(found.getInt(found.getColumnIndex(MovieGenreContract.COLUMN_GENRE_ID))).isEqualTo(2);
        assertThat(found.moveToNext()).isFalse();
    }
}