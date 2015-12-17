package com.github.xavierlepretre.tmdb.sql.movie;

import android.annotation.SuppressLint;
import android.database.MatrixCursor;

import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.github.xavierlepretre.tmdb.model.movie.CollectionId;
import com.github.xavierlepretre.tmdb.model.movie.ImdbId;
import com.github.xavierlepretre.tmdb.model.movie.Movie;
import com.github.xavierlepretre.tmdb.model.movie.MovieContract;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;

public class MovieCursorTest
{
    private static final String[] COLUMNS = new String[]{
            MovieContract.COLUMN_ADULT,
            MovieContract.COLUMN_BACKDROP_PATH,
            MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID,
            MovieContract.COLUMN_BUDGET,
            MovieContract.COLUMN_HOMEPAGE,
            MovieContract._ID,
            MovieContract.COLUMN_IMDB_ID,
            MovieContract.COLUMN_ORIGINAL_LANGUAGE,
            MovieContract.COLUMN_ORIGINAL_TITLE,
            MovieContract.COLUMN_OVERVIEW,
            MovieContract.COLUMN_POPULARITY,
            MovieContract.COLUMN_POSTER_PATH,
            MovieContract.COLUMN_RELEASE_DATE,
            MovieContract.COLUMN_REVENUE,
            MovieContract.COLUMN_RUNTIME,
            MovieContract.COLUMN_STATUS,
            MovieContract.COLUMN_TAGLINE,
            MovieContract.COLUMN_TITLE,
            MovieContract.COLUMN_VIDEO,
            MovieContract.COLUMN_VOTE_AVERAGE,
            MovieContract.COLUMN_VOTE_COUNT
    };
    private static final String[] VALUES = new String[]{
            "1", // adult
            "/path.jpg", // backdropPath
            "98", // belongsToCollectionId
            "1000002", // budget
            "homepage url", // homepage
            "23", // id
            "5a91be", // imdbId
            "en", // originalLanguage
            "movie1", // originalTitle
            "overview", // overview
            "2.3", // popularity
            "/poster.jpg", // posterPath
            "2011-12-20", // releaseDate
            "200000", // revenue
            "120", // runtime
            "released", //status
            "tagline", // tagline
            "title", // title
            "1", // video
            "4.5", // voteAverage
            "400" // voteCount
    };

    @Test
    public void mayCreateMovie() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(VALUES);
        MovieCursor entityCursor = new MovieCursor(cursor);
        entityCursor.moveToFirst();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Movie movie = entityCursor.getMovie();
        assertThat(movie.getAdult()).isTrue();
        assertThat(movie.getBackdropPath()).isEqualTo(new ImagePath("/path.jpg"));
        assertThat(movie.getBelongsToCollectionId()).isEqualTo(new CollectionId(98));
        assertThat(movie.getBudget()).isEqualTo(1000002L);
        assertThat(movie.getHomepage()).isEqualTo("homepage url");
        assertThat(movie.getId()).isEqualTo(new MovieId(23));
        assertThat(movie.getImdbId()).isEqualTo(new ImdbId("5a91be"));
        assertThat(movie.getOriginalLanguage()).isEqualTo(LanguageCode.en);
        assertThat(movie.getOriginalTitle()).isEqualTo("movie1");
        assertThat(movie.getOverview()).isEqualTo("overview");
        assertThat(movie.getPopularity()).isEqualTo(2.3f);
        assertThat(movie.getPosterPath()).isEqualTo(new ImagePath("/poster.jpg"));
        assertThat(movie.getReleaseDate()).isEqualTo(formatter.parse("2011-12-20"));
        assertThat(movie.getRevenue()).isEqualTo(200000L);
        assertThat(movie.getRuntime()).isEqualTo(120);
        assertThat(movie.getStatus()).isEqualTo("released");
        assertThat(movie.getTagline()).isEqualTo("tagline");
        assertThat(movie.getTitle()).isEqualTo("title");
        assertThat(movie.getVideo()).isTrue();
        assertThat(movie.getVoteAverage()).isEqualTo(4.5f);
        assertThat(movie.getVoteCount()).isEqualTo(400);
    }

    @Test
    public void mayCreateMovieWithMissing() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(new String[0]);
        cursor.addRow(new String[0]);
        MovieCursor entityCursor = new MovieCursor(cursor);
        entityCursor.moveToFirst();

        Movie movie = entityCursor.getMovie();
        assertThat(movie.getAdult()).isNull();
        assertThat(movie.getBackdropPath()).isNull();
        assertThat(movie.getBelongsToCollectionId()).isNull();
        assertThat(movie.getBudget()).isNull();
        assertThat(movie.getHomepage()).isNull();
        assertThat(movie.getId()).isNull();
        assertThat(movie.getImdbId()).isNull();
        assertThat(movie.getOriginalLanguage()).isNull();
        assertThat(movie.getOriginalTitle()).isNull();
        assertThat(movie.getOverview()).isNull();
        assertThat(movie.getPopularity()).isNull();
        assertThat(movie.getPosterPath()).isNull();
        assertThat(movie.getReleaseDate()).isNull();
        assertThat(movie.getRevenue()).isNull();
        assertThat(movie.getRuntime()).isNull();
        assertThat(movie.getStatus()).isNull();
        assertThat(movie.getTagline()).isNull();
        assertThat(movie.getTitle()).isNull();
        assertThat(movie.getVideo()).isNull();
        assertThat(movie.getVoteAverage()).isNull();
        assertThat(movie.getVoteCount()).isNull();
    }

    @Test
    public void mayCreateMovieWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(new String[COLUMNS.length]);
        MovieCursor entityCursor = new MovieCursor(cursor);
        entityCursor.moveToFirst();

        Movie movie = entityCursor.getMovie();
        assertThat(movie.getAdult()).isNull();
        assertThat(movie.getBackdropPath()).isNull();
        assertThat(movie.getBelongsToCollectionId()).isNull();
        assertThat(movie.getBudget()).isNull();
        assertThat(movie.getHomepage()).isNull();
        assertThat(movie.getId()).isNull();
        assertThat(movie.getImdbId()).isNull();
        assertThat(movie.getOriginalLanguage()).isNull();
        assertThat(movie.getOriginalTitle()).isNull();
        assertThat(movie.getOverview()).isNull();
        assertThat(movie.getPopularity()).isNull();
        assertThat(movie.getPosterPath()).isNull();
        assertThat(movie.getReleaseDate()).isNull();
        assertThat(movie.getRevenue()).isNull();
        assertThat(movie.getRuntime()).isNull();
        assertThat(movie.getStatus()).isNull();
        assertThat(movie.getTagline()).isNull();
        assertThat(movie.getTitle()).isNull();
        assertThat(movie.getVideo()).isNull();
        assertThat(movie.getVoteAverage()).isNull();
        assertThat(movie.getVoteCount()).isNull();
    }
}