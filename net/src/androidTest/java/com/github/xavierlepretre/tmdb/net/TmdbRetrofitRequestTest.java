package com.github.xavierlepretre.tmdb.net;

import android.annotation.SuppressLint;
import android.support.test.runner.AndroidJUnit4;
import android.test.FlakyTest;

import com.github.xavierlepretre.tmdb.TmdbDtoConstants.Movie;
import com.github.xavierlepretre.tmdb.helper.IsConnectedTestRule;
import com.github.xavierlepretre.tmdb.model.AppendableRequest;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.discover.DiscoverMoviesDTO;
import com.github.xavierlepretre.tmdb.model.i18n.TranslationsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.github.xavierlepretre.tmdb.model.image.ImagesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.movie.AlternativeTitlesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;
import com.github.xavierlepretre.tmdb.model.movie.MovieDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.github.xavierlepretre.tmdb.model.movie.MovieWithExtraDTO;
import com.github.xavierlepretre.tmdb.model.people.CastId;
import com.github.xavierlepretre.tmdb.model.people.CreditId;
import com.github.xavierlepretre.tmdb.model.people.CreditsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.people.PersonId;
import com.github.xavierlepretre.tmdb.model.rate.ReviewId;
import com.github.xavierlepretre.tmdb.model.rate.ReviewsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.show.ReleasesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.show.VideoId;
import com.github.xavierlepretre.tmdb.model.show.VideosWithIdDTO;
import com.github.xavierlepretre.tmdb.model.tag.KeywordId;
import com.github.xavierlepretre.tmdb.model.tag.KeywordsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.tag.ListId;
import com.github.xavierlepretre.tmdb.model.tag.ListsWithIdDTO;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.LanguageCode;

import org.fest.assertions.data.Offset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import retrofit.Response;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class TmdbRetrofitRequestTest
{
    private TmdbRetrofit retrofit;
    private String apiKey;
    private DateFormat formatter;

    @Rule
    public IsConnectedTestRule isConnectedTestRule = new IsConnectedTestRule();

    @Before @SuppressLint("SimpleDateFormat")
    public void setUp()
    {
        this.retrofit = new TmdbRetrofitFactory().create();
        this.apiKey = BuildConfig.TMDB_API_KEY;
        this.formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetConfiguration() throws Exception
    {
        Response<ConfigurationDTO> response = retrofit
                .getConfiguration(apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.body().getImagesConf().getBaseUrl()).isEqualTo("http://image.tmdb.org/t/p/");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canDiscoverMovies() throws Exception
    {
        Response<DiscoverMoviesDTO> response = retrofit
                .discoverMovies(apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetMovie() throws Exception
    {
        Response<MovieDTO> response = retrofit
                .getMovie(206647, apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        MovieDTO dto = response.body();
        assertThat(dto.getAdult()).isFalse();
        assertThat(dto.getBackdropPath()).isEqualTo(new ImagePath("/qSc4L05AnHbMpSk0bsHuX25vX4V.jpg"));
        assertThat(dto.getGenreIds()).containsOnly(new GenreId(28), new GenreId(12), new GenreId(80));
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getOriginalLanguage()).isEqualTo(LanguageCode.en);
        assertThat(dto.getOriginalTitle()).isEqualTo("Spectre");
        assertThat(dto.getOverview()).startsWith("A cryptic message from Bond’s");
        assertThat(dto.getReleaseDate()).isEqualTo(formatter.parse("2015-10-26"));
        assertThat(dto.getPosterPath()).isEqualTo(new ImagePath("/mSvpKOWbyFtLro9BjfEGqUw5dXE.jpg"));
        assertThat(dto.getPopularity()).isGreaterThan(10f);
        assertThat(dto.getTitle()).isEqualTo("Spectre");
        assertThat(dto.getVideo()).isFalse();
        assertThat(dto.getVoteAverage()).isGreaterThan(5f);
        assertThat(dto.getVoteCount()).isGreaterThanOrEqualTo(453);
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetAlternativeTitles() throws Exception
    {
        Response<AlternativeTitlesWithIdDTO> response = retrofit
                .getMovieAlternativeTitles(206647, apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        AlternativeTitlesWithIdDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getTitles().size()).isGreaterThanOrEqualTo(6);
        assertThat(dto.getTitles().get(1).getIso3166Dash1()).isEqualTo(CountryCode.ES);
        assertThat(dto.getTitles().get(1).getTitle()).isEqualTo("007 James Bond: Spectre");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetCredits() throws Exception
    {
        Response<CreditsWithIdDTO> response = retrofit
                .getMovieCredits(206647, apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        CreditsWithIdDTO dto = response.body();
        assertThat(dto.getCast().get(0).getCastId()).isEqualTo(new CastId(1));
        assertThat(dto.getCast().get(0).getCharacter()).isEqualTo("James Bond");
        assertThat(dto.getCast().get(0).getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d6b"));
        assertThat(dto.getCast().get(0).getId()).isEqualTo(new PersonId(8784));
        assertThat(dto.getCast().get(0).getName()).isEqualTo("Daniel Craig");
        assertThat(dto.getCast().get(0).getOrder()).isEqualTo(0);
        assertThat(dto.getCast().get(0).getProfilePath()).isEqualTo(new ImagePath("/mr6cdu6lLRscfFUv8onVWZqaRdZ.jpg"));
        assertThat(dto.getCrew().size()).isGreaterThanOrEqualTo(15);
        assertThat(dto.getCrew().get(0).getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d71"));
        assertThat(dto.getCrew().get(0).getDepartment()).isEqualTo("Writing");
        assertThat(dto.getCrew().get(0).getId()).isEqualTo(new PersonId(9856));
        assertThat(dto.getCrew().get(0).getJob()).isEqualTo("Characters");
        assertThat(dto.getCrew().get(0).getName()).isEqualTo("Ian Fleming");
        assertThat(dto.getCrew().get(0).getProfilePath()).isEqualTo(new ImagePath("/91U37Em6Ru87DiAPMdsocGKyQ0W.jpg"));
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetImages() throws Exception
    {
        Response<ImagesWithIdDTO> response = retrofit
                .getMovieImages(206647, apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        ImagesWithIdDTO dto = response.body();
        assertThat(dto.getBackdrops().size()).isGreaterThanOrEqualTo(11);
        assertThat(dto.getBackdrops().get(9).getAspectRatio()).isEqualTo(1.77777777777778f, Offset.offset(0.001f));
        assertThat(dto.getBackdrops().get(9).getFilePath()).isEqualTo(new ImagePath("/cNiO22mtARcyeNivzeON9sOvr65.jpg"));
        assertThat(dto.getBackdrops().get(9).getHeight()).isEqualTo(720);
        assertThat(dto.getBackdrops().get(9).getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(dto.getBackdrops().get(9).getVoteAverage()).isEqualTo(5.18601190476191f, Offset.offset(0.001f));
        assertThat(dto.getBackdrops().get(9).getVoteCount()).isEqualTo(1);
        assertThat(dto.getBackdrops().get(9).getWidth()).isEqualTo(1280);
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getPosters().size()).isGreaterThanOrEqualTo(26);
        assertThat(dto.getPosters().get(4).getAspectRatio()).isEqualTo(0.666666666666667f, Offset.offset(0.001f));
        assertThat(dto.getPosters().get(4).getFilePath()).isEqualTo(new ImagePath("/zHx5KaKaQdZZnEUgtjcIdNcB3ka.jpg"));
        assertThat(dto.getPosters().get(4).getHeight()).isEqualTo(3000);
        assertThat(dto.getPosters().get(4).getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(dto.getPosters().get(4).getVoteAverage()).isEqualTo(5.31292517006803f, Offset.offset(1f));
        assertThat(dto.getPosters().get(4).getVoteCount()).isGreaterThanOrEqualTo(15);
        assertThat(dto.getPosters().get(4).getWidth()).isEqualTo(2000);
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetKeywords() throws Exception
    {
        Response<KeywordsWithIdDTO> response = retrofit
                .getMovieKeywords(206647, apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        KeywordsWithIdDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getKeywords().size()).isEqualTo(1);
        assertThat(dto.getKeywords().get(0).getId()).isEqualTo(new KeywordId(12360));
        assertThat(dto.getKeywords().get(0).getName()).isEqualTo("james bond");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetLists() throws Exception
    {
        Response<ListsWithIdDTO> response = retrofit
                .getMovieLists(206647, apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        ListsWithIdDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getResults().size()).isEqualTo(20);
        assertThat(dto.getResults().get(0).getDescription()).isEqualTo("James Bond");
        assertThat(dto.getResults().get(0).getFavoriteCount()).isGreaterThanOrEqualTo(5);
        assertThat(dto.getResults().get(0).getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(dto.getResults().get(0).getItemCount()).isGreaterThanOrEqualTo(26);
        assertThat(dto.getResults().get(0).getId()).isEqualTo(new ListId("5308b87fc3a36842010027be"));
        assertThat(dto.getResults().get(0).getName()).isEqualTo("James Bond - Movie Collection");
        assertThat(dto.getResults().get(0).getPosterPath()).isEqualTo(new ImagePath("/jHt3L6rxboCMHULYGdmv6TqjvZr.jpg"));
        assertThat(dto.getTotalPages()).isGreaterThanOrEqualTo(2);
        assertThat(dto.getTotalResults()).isGreaterThanOrEqualTo(29);
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetReleases() throws Exception
    {
        Response<ReleasesWithIdDTO> response = retrofit
                .getMovieReleases(206647, apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        ReleasesWithIdDTO dto = response.body();
        assertThat(dto.getCountries().size()).isEqualTo(59);
        assertThat(dto.getCountries().get(0).getCertification()).isEmpty();
        assertThat(dto.getCountries().get(0).getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(dto.getCountries().get(0).isPrimary()).isTrue();
        assertThat(dto.getCountries().get(0).getReleaseDate()).isEqualTo(formatter.parse("2015-10-26"));
        assertThat(dto.getCountries().get(1).getCertification()).isEqualTo("PG-13");
        assertThat(dto.getCountries().get(1).getIso3166Dash1()).isEqualTo(CountryCode.US);
        assertThat(dto.getCountries().get(1).isPrimary()).isFalse();
        assertThat(dto.getCountries().get(1).getReleaseDate()).isEqualTo(formatter.parse("2015-11-06"));
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetReviews() throws Exception
    {
        Response<ReviewsWithIdDTO> response = retrofit
                .getMovieReviews(206647, apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        ReviewsWithIdDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getResults().size()).isGreaterThanOrEqualTo(3);
        assertThat(dto.getResults().get(0).getAuthor()).isEqualTo("cutprintchris");
        assertThat(dto.getResults().get(0).getContent()).startsWith("<a href=\"http://www.cutprintfilm.com/r");
        assertThat(dto.getResults().get(0).getId()).isEqualTo(new ReviewId("563e06159251413b1300c821"));
        assertThat(dto.getResults().get(0).getUrl()).isEqualTo("http://j.mp/1MPodnZ");
        assertThat(dto.getTotalPages()).isGreaterThanOrEqualTo(1);
        assertThat(dto.getTotalResults()).isGreaterThanOrEqualTo(2);
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetSimilar() throws Exception
    {
        Response<DiscoverMoviesDTO> response = retrofit
                .getMovieSimilar(206647, apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        DiscoverMoviesDTO dto = response.body();
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getResults().size()).isEqualTo(20);
        int pickedSimilar = 1;
        assertThat(dto.getResults().get(pickedSimilar).getAdult()).isEqualTo(false);
        assertThat(dto.getResults().get(pickedSimilar).getBackdropPath()).isEqualTo(new ImagePath("/iyD72nJFBGbEIrpQjzdhE3wFxPL.jpg"));
        assertThat(dto.getResults().get(pickedSimilar).getGenreIds()).containsOnly(
                new GenreId(12),
                new GenreId(28),
                new GenreId(53));
        assertThat(dto.getResults().get(pickedSimilar).getId()).isEqualTo(new MovieId(658));
        assertThat(dto.getResults().get(pickedSimilar).getOriginalLanguage()).isEqualTo(LanguageCode.en);
        assertThat(dto.getResults().get(pickedSimilar).getOriginalTitle()).isEqualTo("Goldfinger");
        assertThat(dto.getResults().get(pickedSimilar).getOverview()).startsWith("Bond is in Miami on holiday when");
        assertThat(dto.getResults().get(pickedSimilar).getPosterPath()).isEqualTo(new ImagePath("/vBNbFU3OS6okJIQBOos1aZXpy2Z.jpg"));
        assertThat(dto.getResults().get(pickedSimilar).getPopularity()).isGreaterThanOrEqualTo(1.8f);
        assertThat(dto.getResults().get(pickedSimilar).getReleaseDate()).isEqualTo(formatter.parse("1964-09-17"));
        assertThat(dto.getResults().get(pickedSimilar).getTitle()).isEqualTo("Goldfinger");
        assertThat(dto.getResults().get(pickedSimilar).getVideo()).isFalse();
        assertThat(dto.getResults().get(pickedSimilar).getVoteAverage()).isGreaterThanOrEqualTo(5);
        assertThat(dto.getResults().get(pickedSimilar).getVoteCount()).isGreaterThanOrEqualTo(356);
        assertThat(dto.getTotalPages()).isEqualTo(3);
        assertThat(dto.getTotalResults()).isGreaterThanOrEqualTo(42);
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetTranslations() throws Exception
    {
        Response<TranslationsWithIdDTO> response = retrofit
                .getMovieTranslations(206647, apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        TranslationsWithIdDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getTranslations().size()).isGreaterThanOrEqualTo(22);
        assertThat(dto.getTranslations().get(1).getEnglishName()).isEqualTo("French");
        assertThat(dto.getTranslations().get(1).getIso639Dash1()).isEqualTo(LanguageCode.fr);
        assertThat(dto.getTranslations().get(1).getName()).isEqualTo("Français");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetVideos() throws Exception
    {
        Response<VideosWithIdDTO> response = retrofit
                .getMovieVideos(206647, apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        VideosWithIdDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getResults().size()).isEqualTo(3);
        assertThat(dto.getResults().get(0).getId()).isEqualTo(new VideoId("5641eb2fc3a3685bdc002e0b"));
        assertThat(dto.getResults().get(0).getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(dto.getResults().get(0).getKey()).isEqualTo("BOVriTeIypQ");
        assertThat(dto.getResults().get(0).getName()).isEqualTo("Spectre Ultimate 007 Teaser");
        assertThat(dto.getResults().get(0).getSite()).isEqualTo("YouTube");
        assertThat(dto.getResults().get(0).getSize()).isEqualTo(1080);
        assertThat(dto.getResults().get(0).getType()).isEqualTo("Teaser");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetMovieWithAllExtras() throws Exception
    {
        Response<MovieWithExtraDTO> response = retrofit
                .getMovie(206647,
                        apiKey,
                        null,
                        Movie.EXTRA_ALTERNATIVE_TITLES + ","
                                + Movie.EXTRA_CREDITS + ","
                                + Movie.EXTRA_IMAGES + ","
                                + Movie.EXTRA_KEYWORDS + ","
                                + Movie.EXTRA_LISTS + ","
                                + Movie.EXTRA_RELEASES + ","
                                + Movie.EXTRA_REVIEWS + ","
                                + Movie.EXTRA_SIMILAR + ","
                                + Movie.EXTRA_TRANSLATIONS + ","
                                + Movie.EXTRA_VIDEOS)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        MovieWithExtraDTO dto = response.body();
        assertThat(dto.getAdult()).isFalse();
        assertThat(dto.getBackdropPath()).isEqualTo(new ImagePath("/qSc4L05AnHbMpSk0bsHuX25vX4V.jpg"));
        assertThat(dto.getGenreIds()).containsOnly(new GenreId(28), new GenreId(12), new GenreId(80));
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getOriginalLanguage()).isEqualTo(LanguageCode.en);
        assertThat(dto.getOriginalTitle()).isEqualTo("Spectre");
        assertThat(dto.getOverview()).startsWith("A cryptic message from Bond’s");
        assertThat(dto.getReleaseDate()).isEqualTo(formatter.parse("2015-10-26"));
        assertThat(dto.getPosterPath()).isEqualTo(new ImagePath("/mSvpKOWbyFtLro9BjfEGqUw5dXE.jpg"));
        assertThat(dto.getPopularity()).isGreaterThan(10f);
        assertThat(dto.getTitle()).isEqualTo("Spectre");
        assertThat(dto.getVideo()).isFalse();
        assertThat(dto.getVoteAverage()).isGreaterThan(5f);
        assertThat(dto.getVoteCount()).isGreaterThanOrEqualTo(453);
        // Alternative titles
        //noinspection ConstantConditions
        assertThat(dto.getAlternativeTitles().getTitles().size()).isGreaterThanOrEqualTo(6);
        assertThat(dto.getAlternativeTitles().getTitles().get(1).getIso3166Dash1()).isEqualTo(CountryCode.ES);
        assertThat(dto.getAlternativeTitles().getTitles().get(1).getTitle()).isEqualTo("007 James Bond: Spectre");
        // Credits
        //noinspection ConstantConditions
        assertThat(dto.getCredits().getCast().get(0).getCastId()).isEqualTo(new CastId(1));
        assertThat(dto.getCredits().getCast().get(0).getCharacter()).isEqualTo("James Bond");
        assertThat(dto.getCredits().getCast().get(0).getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d6b"));
        assertThat(dto.getCredits().getCast().get(0).getId()).isEqualTo(new PersonId(8784));
        assertThat(dto.getCredits().getCast().get(0).getName()).isEqualTo("Daniel Craig");
        assertThat(dto.getCredits().getCast().get(0).getOrder()).isEqualTo(0);
        assertThat(dto.getCredits().getCast().get(0).getProfilePath()).isEqualTo(new ImagePath("/mr6cdu6lLRscfFUv8onVWZqaRdZ.jpg"));
        assertThat(dto.getCredits().getCrew().size()).isGreaterThanOrEqualTo(15);
        assertThat(dto.getCredits().getCrew().get(0).getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d71"));
        assertThat(dto.getCredits().getCrew().get(0).getDepartment()).isEqualTo("Writing");
        assertThat(dto.getCredits().getCrew().get(0).getId()).isEqualTo(new PersonId(9856));
        assertThat(dto.getCredits().getCrew().get(0).getJob()).isEqualTo("Characters");
        assertThat(dto.getCredits().getCrew().get(0).getName()).isEqualTo("Ian Fleming");
        assertThat(dto.getCredits().getCrew().get(0).getProfilePath()).isEqualTo(new ImagePath("/91U37Em6Ru87DiAPMdsocGKyQ0W.jpg"));
        // Images
        //noinspection ConstantConditions
        assertThat(dto.getImages().getBackdrops().size()).isGreaterThanOrEqualTo(11);
        assertThat(dto.getImages().getBackdrops().get(9).getAspectRatio()).isEqualTo(1.77777777777778f, Offset.offset(0.001f));
        assertThat(dto.getImages().getBackdrops().get(9).getFilePath()).isEqualTo(new ImagePath("/cNiO22mtARcyeNivzeON9sOvr65.jpg"));
        assertThat(dto.getImages().getBackdrops().get(9).getHeight()).isEqualTo(720);
        assertThat(dto.getImages().getBackdrops().get(9).getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(dto.getImages().getBackdrops().get(9).getVoteAverage()).isEqualTo(5.18601190476191f, Offset.offset(0.001f));
        assertThat(dto.getImages().getBackdrops().get(9).getVoteCount()).isEqualTo(1);
        assertThat(dto.getImages().getBackdrops().get(9).getWidth()).isEqualTo(1280);
        assertThat(dto.getImages().getPosters().size()).isGreaterThanOrEqualTo(26);
        assertThat(dto.getImages().getPosters().get(4).getAspectRatio()).isEqualTo(0.666666666666667f, Offset.offset(0.001f));
        assertThat(dto.getImages().getPosters().get(4).getFilePath()).isEqualTo(new ImagePath("/zHx5KaKaQdZZnEUgtjcIdNcB3ka.jpg"));
        assertThat(dto.getImages().getPosters().get(4).getHeight()).isEqualTo(3000);
        assertThat(dto.getImages().getPosters().get(4).getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(dto.getImages().getPosters().get(4).getVoteAverage()).isEqualTo(5.31292517006803f, Offset.offset(1f));
        assertThat(dto.getImages().getPosters().get(4).getVoteCount()).isGreaterThanOrEqualTo(15);
        assertThat(dto.getImages().getPosters().get(4).getWidth()).isEqualTo(2000);
        // Keywords
        //noinspection ConstantConditions
        assertThat(dto.getKeywords().getKeywords().size()).isGreaterThanOrEqualTo(1);
        assertThat(dto.getKeywords().getKeywords().get(0).getId()).isEqualTo(new KeywordId(12360));
        assertThat(dto.getKeywords().getKeywords().get(0).getName()).isEqualTo("james bond");
        // Lists
        //noinspection ConstantConditions
        assertThat(dto.getLists().getPage()).isEqualTo(1);
        assertThat(dto.getLists().getResults().size()).isEqualTo(20);
        assertThat(dto.getLists().getResults().get(0).getDescription()).isEqualTo("James Bond");
        assertThat(dto.getLists().getResults().get(0).getFavoriteCount()).isGreaterThanOrEqualTo(5);
        assertThat(dto.getLists().getResults().get(0).getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(dto.getLists().getResults().get(0).getItemCount()).isGreaterThanOrEqualTo(26);
        assertThat(dto.getLists().getResults().get(0).getId()).isEqualTo(new ListId("5308b87fc3a36842010027be"));
        assertThat(dto.getLists().getResults().get(0).getName()).isEqualTo("James Bond - Movie Collection");
        assertThat(dto.getLists().getResults().get(0).getPosterPath()).isEqualTo(new ImagePath("/jHt3L6rxboCMHULYGdmv6TqjvZr.jpg"));
        assertThat(dto.getLists().getTotalPages()).isGreaterThanOrEqualTo(2);
        assertThat(dto.getLists().getTotalResults()).isGreaterThanOrEqualTo(29);
        // Releases
        //noinspection ConstantConditions
        assertThat(dto.getReleases().getCountries().size()).isEqualTo(59);
        assertThat(dto.getReleases().getCountries().get(0).getCertification()).isEmpty();
        assertThat(dto.getReleases().getCountries().get(0).getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(dto.getReleases().getCountries().get(0).isPrimary()).isTrue();
        assertThat(dto.getReleases().getCountries().get(0).getReleaseDate()).isEqualTo(formatter.parse("2015-10-26"));
        assertThat(dto.getReleases().getCountries().get(1).getCertification()).isEqualTo("PG-13");
        assertThat(dto.getReleases().getCountries().get(1).getIso3166Dash1()).isEqualTo(CountryCode.US);
        assertThat(dto.getReleases().getCountries().get(1).isPrimary()).isFalse();
        assertThat(dto.getReleases().getCountries().get(1).getReleaseDate()).isEqualTo(formatter.parse("2015-11-06"));
        // Reviews
        //noinspection ConstantConditions
        assertThat(dto.getReviews().getPage()).isEqualTo(1);
        assertThat(dto.getReviews().getResults().size()).isGreaterThanOrEqualTo(3);
        assertThat(dto.getReviews().getResults().get(0).getAuthor()).isEqualTo("cutprintchris");
        assertThat(dto.getReviews().getResults().get(0).getContent()).startsWith("<a href=\"http://www.cutprintfilm.com/r");
        assertThat(dto.getReviews().getResults().get(0).getId()).isEqualTo(new ReviewId("563e06159251413b1300c821"));
        assertThat(dto.getReviews().getResults().get(0).getUrl()).isEqualTo("http://j.mp/1MPodnZ");
        assertThat(dto.getReviews().getTotalPages()).isGreaterThanOrEqualTo(1);
        assertThat(dto.getReviews().getTotalResults()).isGreaterThanOrEqualTo(2);
        // Similar
        //noinspection ConstantConditions
        assertThat(dto.getSimilar().getPage()).isEqualTo(1);
        assertThat(dto.getSimilar().getResults().size()).isEqualTo(20);
        int pickedSimilar = 1;
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getAdult()).isEqualTo(false);
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getBackdropPath()).isEqualTo(new ImagePath("/iyD72nJFBGbEIrpQjzdhE3wFxPL.jpg"));
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getGenreIds()).containsOnly(
                new GenreId(12),
                new GenreId(28),
                new GenreId(53));
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getId()).isEqualTo(new MovieId(658));
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getOriginalLanguage()).isEqualTo(LanguageCode.en);
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getOriginalTitle()).isEqualTo("Goldfinger");
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getOverview()).startsWith("Bond is in Miami on holiday when");
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getPosterPath()).isEqualTo(new ImagePath("/vBNbFU3OS6okJIQBOos1aZXpy2Z.jpg"));
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getPopularity()).isGreaterThanOrEqualTo(1.8f);
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getReleaseDate()).isEqualTo(formatter.parse("1964-09-17"));
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getTitle()).isEqualTo("Goldfinger");
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getVideo()).isFalse();
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getVoteAverage()).isGreaterThanOrEqualTo(5);
        assertThat(dto.getSimilar().getResults().get(pickedSimilar).getVoteCount()).isGreaterThanOrEqualTo(356);
        assertThat(dto.getSimilar().getTotalPages()).isEqualTo(3);
        assertThat(dto.getSimilar().getTotalResults()).isGreaterThanOrEqualTo(42);
        // Translations
        //noinspection ConstantConditions
        assertThat(dto.getTranslations().getTranslations().size()).isGreaterThanOrEqualTo(22);
        int pickedTranslation = 1;
        assertThat(dto.getTranslations().getTranslations().get(pickedTranslation).getEnglishName()).isEqualTo("French");
        assertThat(dto.getTranslations().getTranslations().get(pickedTranslation).getIso639Dash1()).isEqualTo(LanguageCode.fr);
        assertThat(dto.getTranslations().getTranslations().get(pickedTranslation).getName()).isEqualTo("Français");
        // Videos
        //noinspection ConstantConditions
        assertThat(dto.getVideos().getResults().size()).isEqualTo(3);
        assertThat(dto.getVideos().getResults().get(0).getId()).isEqualTo(new VideoId("5641eb2fc3a3685bdc002e0b"));
        assertThat(dto.getVideos().getResults().get(0).getIso639Dash1()).isEqualTo(LanguageCode.en);
        assertThat(dto.getVideos().getResults().get(0).getKey()).isEqualTo("BOVriTeIypQ");
        assertThat(dto.getVideos().getResults().get(0).getName()).isEqualTo("Spectre Ultimate 007 Teaser");
        assertThat(dto.getVideos().getResults().get(0).getSite()).isEqualTo("YouTube");
        assertThat(dto.getVideos().getResults().get(0).getSize()).isEqualTo(1080);
        assertThat(dto.getVideos().getResults().get(0).getType()).isEqualTo("Teaser");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetLatestMovie() throws Exception
    {
        Response<MovieDTO> response = retrofit
                .getLatestMovie(apiKey)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        MovieDTO dto = response.body();
        assertThat(dto.getId().getId()).isGreaterThanOrEqualTo(368669);
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetLatestMovieWithExtras() throws Exception
    {
        Response<MovieWithExtraDTO> response = retrofit
                .getLatestMovie(apiKey, null, AppendableRequest.Movie.IMAGES.toString())
                .execute();
        assertThat(response.isSuccess()).isTrue();
        MovieWithExtraDTO dto = response.body();
        assertThat(dto.getId().getId()).isGreaterThanOrEqualTo(368669);
        assertThat(dto.getImages()).isNotNull();
    }
}
