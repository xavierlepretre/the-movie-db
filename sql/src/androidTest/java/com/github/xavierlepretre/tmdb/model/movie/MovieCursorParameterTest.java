package com.github.xavierlepretre.tmdb.model.movie;

import android.annotation.SuppressLint;
import android.database.MatrixCursor;

import com.github.xavierlepretre.tmdb.model.ParameterColumnValue;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class MovieCursorParameterTest
{
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{MovieContract.COLUMN_ADULT, "1"},
            new String[]{MovieContract.COLUMN_BACKDROP_PATH, "/path.jpg"},
            new String[]{MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, "98"},
            new String[]{MovieContract.COLUMN_BUDGET, "1000002"},
            new String[]{MovieContract._ID, "23"},
            new String[]{MovieContract.COLUMN_IMDB_ID, "5a91be"},
            new String[]{MovieContract.COLUMN_ORIGINAL_LANGUAGE, "en"},
            new String[]{MovieContract.COLUMN_RELEASE_DATE, "2011-12-20"}
    };

    @Parameterized.Parameter
    public ParameterColumnValue parameter;

    @Parameterized.Parameters()
    public static ParameterColumnValue[] getParameters()
    {
        return ParameterColumnValue.getPossibleParameters(POTENTIAL_COLUMNS);
    }

    @Test
    public void mayCreateMovieWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(parameter.columns.toArray(new String[parameter.columns.size()]));
        for (List<String> row : parameter.rows)
        {
            cursor.addRow(row);
        }
        MovieCursor entityCursor = new MovieCursor(cursor);
        entityCursor.moveToFirst();

        Movie movie = entityCursor.getMovie();
        assertThat(movie.getAdult()).isEqualTo(
                parameter.columns.contains(MovieContract.COLUMN_ADULT)
                        ? true
                        : null);
        assertThat(movie.getBackdropPath()).isEqualTo(
                parameter.columns.contains(MovieContract.COLUMN_BACKDROP_PATH)
                        ? new ImagePath("/path.jpg")
                        : null);
        assertThat(movie.getBelongsToCollectionId()).isEqualTo(
                parameter.columns.contains(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID)
                        ? new CollectionId(98)
                        : null);
        assertThat(movie.getBudget()).isEqualTo(
                parameter.columns.contains(MovieContract.COLUMN_BUDGET)
                        ? 1000002L
                        : null);
        assertThat(movie.getId()).isEqualTo(
                parameter.columns.contains(MovieContract._ID)
                        ? new MovieId(23)
                        : null);
        assertThat(movie.getImdbId()).isEqualTo(
                parameter.columns.contains(MovieContract.COLUMN_IMDB_ID)
                        ? new ImdbId("5a91be")
                        : null);
        assertThat(movie.getOriginalLanguage()).isEqualTo(
                parameter.columns.contains(MovieContract.COLUMN_ORIGINAL_LANGUAGE)
                        ? LanguageCode.en
                        : null);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        assertThat(movie.getReleaseDate()).isEqualTo(
                parameter.columns.contains(MovieContract.COLUMN_RELEASE_DATE)
                        ? formatter.parse("2011-12-20")
                        : null);
    }
}