package com.github.xavierlepretre.tmdb.model.movie;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.Vector;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class MovieGenreContentValuesFactoryTest
{
    private SimpleDateFormat formatter;
    private MovieGenreContentValuesFactory factory;

    @SuppressLint("SimpleDateFormat") @Before
    public void setUp() throws Exception
    {
        formatter = new SimpleDateFormat(MovieContract.RELEASE_DATE_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone(MovieContract.RELEASE_DATE_TIME_ZONE));
        factory = spy(new MovieGenreContentValuesFactory());
    }

    @NonNull private MovieShortDTO getMovieShort1() throws Exception
    {
        return new MovieShortDTO(
                true,
                new ImagePath("/backdrop1.jpg"),
                Arrays.asList(
                        new GenreId(3),
                        new GenreId(4)),
                new MovieId(12),
                LanguageCode.en,
                "original_title1",
                "overview1",
                2.3F,
                new ImagePath("/poster1.jpg"),
                formatter.parse("2011-11-03"),
                "title1",
                true,
                3.4F,
                46);
    }

    @Test
    public void createFromMovieShort_isOk() throws Exception
    {
        Vector<ContentValues> values = factory.createFrom(getMovieShort1());
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0).getAsLong(MovieGenreContract.COLUMN_MOVIE_ID)).isEqualTo(12L);
        assertThat(values.get(0).getAsInteger(MovieGenreContract.COLUMN_GENRE_ID)).isEqualTo(3);
        assertThat(values.get(1).getAsLong(MovieGenreContract.COLUMN_MOVIE_ID)).isEqualTo(12L);
        assertThat(values.get(1).getAsInteger(MovieGenreContract.COLUMN_GENRE_ID)).isEqualTo(4);
    }
}