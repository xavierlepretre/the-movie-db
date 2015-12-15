package com.github.xavierlepretre.tmdb.model.movie;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.i18n.SpokenLanguageDTO;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyDTO;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyId;
import com.github.xavierlepretre.tmdb.model.production.ProductionCountryDTO;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.TimeZone;
import java.util.Vector;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

public class MovieContentValuesFactoryTest
{
    private SimpleDateFormat formatter;
    private MovieContentValuesFactory factory;

    @Before
    public void setUp() throws Exception
    {
        formatter = new SimpleDateFormat(MovieContract.RELEASE_DATE_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone(MovieContract.RELEASE_DATE_TIME_ZONE));
        factory = spy(new MovieContentValuesFactory());
    }

    @NonNull  private MovieDTO getMovie1() throws Exception
    {
        return new MovieDTO(
                true,
                new ImagePath("/backdrop1.jpg"),
                new CollectionDTO(
                        new ImagePath("/coll_backdrop1.jpg"),
                        new CollectionId(33),
                        "collection1",
                        new ImagePath("/coll_poster1.jpg")),
                100000,
                Arrays.asList(
                        new GenreDTO(new GenreId(3), "Adventure"),
                        new GenreDTO(new GenreId(4), "Action")),
                "homepage1",
                new MovieId(12),
                new ImdbId("imdb1"),
                LanguageCode.en,
                "original_title1",
                "overview1",
                2.3F,
                new ImagePath("/poster1.jpg"),
                Arrays.asList(
                        new ProductionCompanyDTO(new ProductionCompanyId(11), "company1"),
                        new ProductionCompanyDTO(new ProductionCompanyId(12), "company2")),
                Arrays.asList(
                        new ProductionCountryDTO(CountryCode.GB, "United Kingdom"),
                        new ProductionCountryDTO(CountryCode.US, "United States of America")),
                formatter.parse("2011-11-03"),
                200000,
                120,
                Collections.singletonList(new SpokenLanguageDTO(LanguageCode.en, "English")),
                "released",
                "tagline1",
                "title1",
                true,
                3.4F,
                46);
    }

    @NonNull  private MovieDTO getMovie2() throws Exception
    {
        return new MovieDTO(
                false,
                new ImagePath("/backdrop2.jpg"),
                new CollectionDTO(
                        new ImagePath("/coll_backdrop2.jpg"),
                        new CollectionId(34),
                        "collection2",
                        new ImagePath("/coll_poster2.jpg")),
                100001,
                Arrays.asList(
                        new GenreDTO(new GenreId(4), "Adventure"),
                        new GenreDTO(new GenreId(5), "Comic")),
                "homepage2",
                new MovieId(13),
                new ImdbId("imdb2"),
                LanguageCode.fr,
                "original_title2",
                "overview2",
                2.5F,
                new ImagePath("/poster2.jpg"),
                Arrays.asList(
                        new ProductionCompanyDTO(new ProductionCompanyId(13), "company3"),
                        new ProductionCompanyDTO(new ProductionCompanyId(14), "company4")),
                Arrays.asList(
                        new ProductionCountryDTO(CountryCode.FR, "France"),
                        new ProductionCountryDTO(CountryCode.DE, "Germany")),
                formatter.parse("2011-10-03"),
                200001,
                121,
                Collections.singletonList(new SpokenLanguageDTO(LanguageCode.fr, "French")),
                "pending",
                "tagline2",
                "title2",
                false,
                5.4F,
                97);
    }

    private void assertThatIsMovie1(@NonNull ContentValues values) throws Exception
    {
        assertThatIsMovieShort1(values);
        assertThat(values.getAsLong(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID)).isEqualTo(33L);
        assertThat(values.getAsLong(MovieContract.COLUMN_BUDGET)).isEqualTo(100000L);
        assertThat(values.getAsString(MovieContract.COLUMN_HOMEPAGE)).isEqualTo("homepage1");
        assertThat(values.getAsString(MovieContract.COLUMN_IMDB_ID)).isEqualTo("imdb1");
        assertThat(values.getAsLong(MovieContract.COLUMN_REVENUE)).isEqualTo(200000L);
        assertThat(values.getAsInteger(MovieContract.COLUMN_RUNTIME)).isEqualTo(120);
        assertThat(values.getAsString(MovieContract.COLUMN_STATUS)).isEqualTo("released");
        assertThat(values.getAsString(MovieContract.COLUMN_TAGLINE)).isEqualTo("tagline1");
    }

    private void assertThatIsMovieShort1(@NonNull ContentValues values) throws Exception
    {
        assertThat(values.getAsBoolean(MovieContract.COLUMN_ADULT)).isTrue();
        assertThat(values.getAsString(MovieContract.COLUMN_BACKDROP_PATH)).isEqualTo("/backdrop1.jpg");
        assertThat(values.getAsLong(MovieContract._ID)).isEqualTo(12L);
        assertThat(values.getAsString(MovieContract.COLUMN_ORIGINAL_LANGUAGE)).isEqualTo("en");
        assertThat(values.getAsString(MovieContract.COLUMN_ORIGINAL_TITLE)).isEqualTo("original_title1");
        assertThat(values.getAsString(MovieContract.COLUMN_OVERVIEW)).isEqualTo("overview1");
        assertThat(values.getAsFloat(MovieContract.COLUMN_POPULARITY)).isEqualTo(2.3F);
        assertThat(values.getAsString(MovieContract.COLUMN_POSTER_PATH)).isEqualTo("/poster1.jpg");
        assertThat(values.getAsString(MovieContract.COLUMN_RELEASE_DATE)).isEqualTo("2011-11-03");
        assertThat(values.getAsString(MovieContract.COLUMN_TITLE)).isEqualTo("title1");
        assertThat(values.getAsBoolean(MovieContract.COLUMN_VIDEO)).isTrue();
        assertThat(values.getAsFloat(MovieContract.COLUMN_VOTE_AVERAGE)).isEqualTo(3.4F);
        assertThat(values.getAsInteger(MovieContract.COLUMN_VOTE_COUNT)).isEqualTo(46);
    }

    private void assertThatIsMovie2(@NonNull ContentValues values) throws Exception
    {
        assertThatIsMovieShort2(values);
        assertThat(values.getAsLong(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID)).isEqualTo(34L);
        assertThat(values.getAsLong(MovieContract.COLUMN_BUDGET)).isEqualTo(100001L);
        assertThat(values.getAsString(MovieContract.COLUMN_HOMEPAGE)).isEqualTo("homepage2");
        assertThat(values.getAsString(MovieContract.COLUMN_IMDB_ID)).isEqualTo("imdb2");
        assertThat(values.getAsLong(MovieContract.COLUMN_REVENUE)).isEqualTo(200001L);
        assertThat(values.getAsInteger(MovieContract.COLUMN_RUNTIME)).isEqualTo(121);
        assertThat(values.getAsString(MovieContract.COLUMN_STATUS)).isEqualTo("pending");
        assertThat(values.getAsString(MovieContract.COLUMN_TAGLINE)).isEqualTo("tagline2");
    }

    private void assertThatIsMovieShort2(@NonNull ContentValues values) throws Exception
    {
        assertThat(values.getAsBoolean(MovieContract.COLUMN_ADULT)).isFalse();
        assertThat(values.getAsString(MovieContract.COLUMN_BACKDROP_PATH)).isEqualTo("/backdrop2.jpg");
        assertThat(values.getAsLong(MovieContract._ID)).isEqualTo(13L);
        assertThat(values.getAsString(MovieContract.COLUMN_ORIGINAL_LANGUAGE)).isEqualTo("fr");
        assertThat(values.getAsString(MovieContract.COLUMN_ORIGINAL_TITLE)).isEqualTo("original_title2");
        assertThat(values.getAsString(MovieContract.COLUMN_OVERVIEW)).isEqualTo("overview2");
        assertThat(values.getAsFloat(MovieContract.COLUMN_POPULARITY)).isEqualTo(2.5F);
        assertThat(values.getAsString(MovieContract.COLUMN_POSTER_PATH)).isEqualTo("/poster2.jpg");
        assertThat(values.getAsString(MovieContract.COLUMN_RELEASE_DATE)).isEqualTo("2011-10-03");
        assertThat(values.getAsString(MovieContract.COLUMN_TITLE)).isEqualTo("title2");
        assertThat(values.getAsBoolean(MovieContract.COLUMN_VIDEO)).isFalse();
        assertThat(values.getAsFloat(MovieContract.COLUMN_VOTE_AVERAGE)).isEqualTo(5.4F);
        assertThat(values.getAsInteger(MovieContract.COLUMN_VOTE_COUNT)).isEqualTo(97);
    }

    @Test
    public void populateFromMovieDto_works() throws Exception
    {
        ContentValues values = new ContentValues();
        MovieDTO dto = getMovie1();
        factory.populate(values, dto);
        assertThat(values.size()).isEqualTo(21);
        assertThatIsMovie1(values);
    }

    @Test
    public void createFromSingleMovieDto_works() throws Exception
    {
        MovieDTO dto = getMovie1();
        ContentValues values = factory.createFrom(dto);
        assertThat(values.size()).isEqualTo(21);
        assertThatIsMovie1(values);
    }

    @Test
    public void createVectorFromMovieDtoCollection_callsSingle() throws Exception
    {
        MovieDTO dto1 = getMovie1();
        MovieDTO dto2 = getMovie2();
        Vector<ContentValues> values = factory.createFrom(Arrays.asList(dto1, dto2));
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0).size()).isEqualTo(21);
        assertThatIsMovie1(values.get(0));
        assertThat(values.get(1).size()).isEqualTo(21);
        assertThatIsMovie2(values.get(1));
    }

    @Test
    public void populateFromMovieShortDto_works() throws Exception
    {
        ContentValues values = new ContentValues();
        MovieShortDTO dto = getMovie1();
        factory.populate(values, dto);
        assertThat(values.size()).isEqualTo(13);
        assertThatIsMovieShort1(values);
    }

    @Test
    public void createFromSingleMovieShortDto_works() throws Exception
    {
        MovieShortDTO dto = getMovie1();
        ContentValues values = factory.createFrom(dto);
        assertThat(values.size()).isEqualTo(13);
        assertThatIsMovieShort1(values);
    }

    @Test
    public void createVectorFromMovieShortDtoCollection_callsSingle() throws Exception
    {
        MovieShortDTO dto1 = getMovie1();
        MovieShortDTO dto2 = getMovie2();
        Vector<ContentValues> values = factory.createFrom(Arrays.asList(dto1, dto2), null);
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0).size()).isEqualTo(13);
        assertThatIsMovieShort1(values.get(0));
        assertThat(values.get(1).size()).isEqualTo(13);
        assertThatIsMovieShort2(values.get(1));
    }

    @Test
    public void populateFromId_works() throws Exception
    {
        ContentValues values = new ContentValues();
        MovieId id = new MovieId(12);
        factory.populate(values, id);
        assertThat(values.size()).isEqualTo(1);
        assertThat(values.getAsInteger(MovieContract._ID)).isEqualTo(12);
    }

    @Test
    public void createFromSingleId_works() throws Exception
    {
        MovieId id = new MovieId(12);
        ContentValues values = factory.createFrom(id);
        assertThat(values.size()).isEqualTo(1);
        assertThat(values.getAsInteger(MovieContract._ID)).isEqualTo(12);
    }

    @Test
    public void createVectorFromIdCollection_callsSingle() throws Exception
    {
        MovieId dto1 = new MovieId(12);
        MovieId dto2 = new MovieId(28);
        Vector<ContentValues> values = factory.createFrom(Arrays.asList(dto1, dto2), null);
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0).size()).isEqualTo(1);
        assertThat(values.get(0).getAsInteger(MovieContract._ID)).isEqualTo(12);
        assertThat(values.get(1).size()).isEqualTo(1);
        assertThat(values.get(1).getAsInteger(MovieContract._ID)).isEqualTo(28);
    }
}