package com.github.xavierlepretre.tmdb.net;

import android.support.test.runner.AndroidJUnit4;
import android.test.FlakyTest;

import com.github.xavierlepretre.tmdb.helper.IsConnectedTestRule;
import com.github.xavierlepretre.tmdb.model.AppendableRequest;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.discover.DiscoverMoviesDTO;
import com.github.xavierlepretre.tmdb.model.i18n.TranslationsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.image.ImagesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.movie.AlternativeTitlesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;
import com.github.xavierlepretre.tmdb.model.movie.MovieDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.github.xavierlepretre.tmdb.model.movie.MovieRequest;
import com.github.xavierlepretre.tmdb.model.movie.MovieRequestParameters;
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

import org.fest.assertions.data.Offset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.Response;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class TmdbServiceRequestTest
{
    private TmdbService service;
    private SimpleDateFormat formatter;

    @Rule
    public IsConnectedTestRule isConnectedTestRule = new IsConnectedTestRule();

    @Before
    public void setUp()
    {
        TmdbRetrofit retrofit = new TmdbRetrofitFactory().create();
        this.service = new TmdbService(retrofit, BuildConfig.TMDB_API_KEY);
        this.formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetConfiguration() throws Exception
    {
        Response<ConfigurationDTO> response = service
                .getConfiguration()
                .execute();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.body().getImagesConf().getBaseUrl()).isEqualTo("http://image.tmdb.org/t/p/");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canDiscoverMovies() throws Exception
    {
        Response<DiscoverMoviesDTO> response = service
                .discoverMovies()
                .execute();
        assertThat(response.isSuccess()).isTrue();
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetMovie() throws Exception
    {
        Response<MovieDTO> response = service
                .getMovie(new MovieId(206647))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        MovieDTO dto = response.body();
        assertThat(dto.getAdult()).isFalse();
        assertThat(dto.getBackdropPath()).isEqualTo("/wVTYlkKPKrljJfugXN7UlLNjtuJ.jpg");
        assertThat(dto.getGenreIds()).containsOnly(new GenreId(28), new GenreId(12), new GenreId(80));
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getOriginalLanguage()).isEqualTo(new Locale("en"));
        assertThat(dto.getOriginalTitle()).isEqualTo("Spectre");
        assertThat(dto.getOverview()).startsWith("A cryptic message from Bond’s");
        assertThat(dto.getReleaseDate()).isEqualTo(formatter.parse("2015-11-06"));
        assertThat(dto.getPosterPath()).isEqualTo("/1n9D32o30XOHMdMWuIT4AaA5ruI.jpg");
        assertThat(dto.getPopularity()).isGreaterThan(50f);
        assertThat(dto.getTitle()).isEqualTo("Spectre");
        assertThat(dto.getVideo()).isFalse();
        assertThat(dto.getVoteAverage()).isGreaterThan(5f);
        assertThat(dto.getVoteCount()).isGreaterThanOrEqualTo(453);
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetAlternativeTitles() throws Exception
    {
        Response<AlternativeTitlesWithIdDTO> response = service
                .getMovieAlternativeTitles(new MovieId(206647))
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
        Response<CreditsWithIdDTO> response = service
                .getMovieCredits(new MovieId(206647))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        CreditsWithIdDTO dto = response.body();
        assertThat(dto.getCast().get(0).getCastId()).isEqualTo(new CastId(1));
        assertThat(dto.getCast().get(0).getCharacter()).isEqualTo("James Bond");
        assertThat(dto.getCast().get(0).getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d6b"));
        assertThat(dto.getCast().get(0).getId()).isEqualTo(new PersonId(8784));
        assertThat(dto.getCast().get(0).getName()).isEqualTo("Daniel Craig");
        assertThat(dto.getCast().get(0).getOrder()).isEqualTo(0);
        assertThat(dto.getCast().get(0).getProfilePath()).isEqualTo("/cO5OUQAMM6a4Rndw5Hc81KgpF5p.jpg");
        assertThat(dto.getCrew().size()).isEqualTo(15);
        assertThat(dto.getCrew().get(0).getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d71"));
        assertThat(dto.getCrew().get(0).getDepartment()).isEqualTo("Writing");
        assertThat(dto.getCrew().get(0).getId()).isEqualTo(new PersonId(9856));
        assertThat(dto.getCrew().get(0).getJob()).isEqualTo("Characters");
        assertThat(dto.getCrew().get(0).getName()).isEqualTo("Ian Fleming");
        assertThat(dto.getCrew().get(0).getProfilePath()).isEqualTo("/91U37Em6Ru87DiAPMdsocGKyQ0W.jpg");
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetImages() throws Exception
    {
        Response<ImagesWithIdDTO> response = service
                .getMovieImages(new MovieId(206647))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        ImagesWithIdDTO dto = response.body();
        assertThat(dto.getBackdrops().size()).isEqualTo(11);
        assertThat(dto.getBackdrops().get(8).getAspectRatio()).isEqualTo(1.77777777777778f, Offset.offset(0.001f));
        assertThat(dto.getBackdrops().get(8).getFilePath()).isEqualTo("/cNiO22mtARcyeNivzeON9sOvr65.jpg");
        assertThat(dto.getBackdrops().get(8).getHeight()).isEqualTo(720);
        assertThat(dto.getBackdrops().get(8).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getBackdrops().get(8).getVoteAverage()).isEqualTo(5.18601190476191f, Offset.offset(0.001f));
        assertThat(dto.getBackdrops().get(8).getVoteCount()).isEqualTo(1);
        assertThat(dto.getBackdrops().get(8).getWidth()).isEqualTo(1280);
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getPosters().size()).isEqualTo(26);
        assertThat(dto.getPosters().get(4).getAspectRatio()).isEqualTo(0.666666666666667f, Offset.offset(0.001f));
        assertThat(dto.getPosters().get(4).getFilePath()).isEqualTo("/1n9D32o30XOHMdMWuIT4AaA5ruI.jpg");
        assertThat(dto.getPosters().get(4).getHeight()).isEqualTo(3000);
        assertThat(dto.getPosters().get(4).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getPosters().get(4).getVoteAverage()).isEqualTo(5.31292517006803f, Offset.offset(0.001f));
        assertThat(dto.getPosters().get(4).getVoteCount()).isEqualTo(7);
        assertThat(dto.getPosters().get(4).getWidth()).isEqualTo(2000);
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetKeywords() throws Exception
    {
        Response<KeywordsWithIdDTO> response = service
                .getMovieKeywords(new MovieId(206647))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        KeywordsWithIdDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getKeywords().size()).isEqualTo(4);
        assertThat(dto.getKeywords().get(0).getId()).isEqualTo(new KeywordId(12360));
        assertThat(dto.getKeywords().get(0).getName()).isEqualTo("james bond");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetLists() throws Exception
    {
        Response<ListsWithIdDTO> response = service
                .getMovieLists(new MovieId(206647))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        ListsWithIdDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getResults().size()).isEqualTo(20);
        assertThat(dto.getResults().get(0).getDescription()).isEqualTo("James Bond");
        assertThat(dto.getResults().get(0).getFavoriteCount()).isGreaterThanOrEqualTo(5);
        assertThat(dto.getResults().get(0).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getResults().get(0).getItemCount()).isGreaterThanOrEqualTo(26);
        assertThat(dto.getResults().get(0).getId()).isEqualTo(new ListId("5308b87fc3a36842010027be"));
        assertThat(dto.getResults().get(0).getName()).isEqualTo("James Bond - Movie Collection");
        assertThat(dto.getResults().get(0).getPosterPath()).isEqualTo("/jHt3L6rxboCMHULYGdmv6TqjvZr.jpg");
        assertThat(dto.getTotalPages()).isGreaterThanOrEqualTo(2);
        assertThat(dto.getTotalResults()).isGreaterThanOrEqualTo(29);
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetReleases() throws Exception
    {
        Response<ReleasesWithIdDTO> response = service
                .getMovieReleases(new MovieId(206647))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        ReleasesWithIdDTO dto = response.body();
        assertThat(dto.getCountries().size()).isEqualTo(59);
        assertThat(dto.getCountries().get(0).getCertification()).isEmpty();
        assertThat(dto.getCountries().get(0).getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(dto.getCountries().get(0).isPrimary()).isFalse();
        assertThat(dto.getCountries().get(0).getReleaseDate()).isEqualTo(formatter.parse("2015-10-26"));
        assertThat(dto.getCountries().get(1).getCertification()).isEqualTo("PG-13");
        assertThat(dto.getCountries().get(1).getIso3166Dash1()).isEqualTo(CountryCode.US);
        assertThat(dto.getCountries().get(1).isPrimary()).isTrue();
        assertThat(dto.getCountries().get(1).getReleaseDate()).isEqualTo(formatter.parse("2015-11-06"));
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetReviews() throws Exception
    {
        Response<ReviewsWithIdDTO> response = service
                .getMovieReviews(new MovieId(206647))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        ReviewsWithIdDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getResults().size()).isEqualTo(2);
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
        Response<DiscoverMoviesDTO> response = service
                .getMovieSimilar(new MovieId(206647))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        DiscoverMoviesDTO dto = response.body();
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getResults().size()).isEqualTo(20);
        assertThat(dto.getResults().get(0).getAdult()).isEqualTo(false);
        assertThat(dto.getResults().get(0).getBackdropPath()).isEqualTo("/iyD72nJFBGbEIrpQjzdhE3wFxPL.jpg");
        assertThat(dto.getResults().get(0).getGenreIds()).containsOnly(
                new GenreId(12),
                new GenreId(28),
                new GenreId(53));
        assertThat(dto.getResults().get(0).getId()).isEqualTo(new MovieId(658));
        assertThat(dto.getResults().get(0).getOriginalLanguage()).isEqualTo(new Locale("en"));
        assertThat(dto.getResults().get(0).getOriginalTitle()).isEqualTo("Goldfinger");
        assertThat(dto.getResults().get(0).getOverview()).startsWith("Bond is in Miami on holiday when");
        assertThat(dto.getResults().get(0).getPosterPath()).isEqualTo("/vBNbFU3OS6okJIQBOos1aZXpy2Z.jpg");
        assertThat(dto.getResults().get(0).getPopularity()).isGreaterThanOrEqualTo(2);
        assertThat(dto.getResults().get(0).getReleaseDate()).isEqualTo(formatter.parse("1964-09-17"));
        assertThat(dto.getResults().get(0).getTitle()).isEqualTo("Goldfinger");
        assertThat(dto.getResults().get(0).getVideo()).isFalse();
        assertThat(dto.getResults().get(0).getVoteAverage()).isGreaterThanOrEqualTo(5);
        assertThat(dto.getResults().get(0).getVoteCount()).isGreaterThanOrEqualTo(356);
        assertThat(dto.getTotalPages()).isEqualTo(3);
        assertThat(dto.getTotalResults()).isEqualTo(44);
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetTranslations() throws Exception
    {
        Response<TranslationsWithIdDTO> response = service
                .getMovieTranslations(new MovieId(206647))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        TranslationsWithIdDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getTranslations().size()).isEqualTo(22);
        assertThat(dto.getTranslations().get(1).getEnglishName()).isEqualTo("French");
        assertThat(dto.getTranslations().get(1).getIso639Dash1()).isEqualTo(new Locale("fr"));
        assertThat(dto.getTranslations().get(1).getName()).isEqualTo("Français");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetVideos() throws Exception
    {
        Response<VideosWithIdDTO> response = service
                .getMovieVideos(new MovieId(206647))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        VideosWithIdDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getResults().size()).isEqualTo(3);
        assertThat(dto.getResults().get(0).getId()).isEqualTo(new VideoId("5641eb2fc3a3685bdc002e0b"));
        assertThat(dto.getResults().get(0).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getResults().get(0).getKey()).isEqualTo("BOVriTeIypQ");
        assertThat(dto.getResults().get(0).getName()).isEqualTo("Spectre Ultimate 007 Trailer 2015 HD");
        assertThat(dto.getResults().get(0).getSite()).isEqualTo("YouTube");
        assertThat(dto.getResults().get(0).getSize()).isEqualTo(1080);
        assertThat(dto.getResults().get(0).getType()).isEqualTo("Trailer");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetMovieWithoutExtra() throws Exception
    {
        Response<MovieWithExtraDTO> response = service
                .getMovie(new MovieRequest(new MovieId(206647)))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        MovieWithExtraDTO dto = response.body();
        assertThat(dto.getAlternativeTitles()).isNull();
        assertThat(dto.getCredits()).isNull();
        assertThat(dto.getImages()).isNull();
        assertThat(dto.getKeywords()).isNull();
        assertThat(dto.getLists()).isNull();
        assertThat(dto.getReleases()).isNull();
        assertThat(dto.getReviews()).isNull();
        assertThat(dto.getSimilar()).isNull();
        assertThat(dto.getTranslations()).isNull();
        assertThat(dto.getVideos()).isNull();
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetMovieWithAllExtras() throws Exception
    {
        Response<MovieWithExtraDTO> response = service
                .getMovie(new MovieRequest(
                        new MovieId(206647),
                        new MovieRequestParameters.Builder()
                                .appendToResponse(AppendableRequest.Movie.ALTERNATIVE_TITLES)
                                .appendToResponse(AppendableRequest.Movie.CREDITS)
                                .appendToResponse(AppendableRequest.Movie.IMAGES)
                                .appendToResponse(AppendableRequest.Movie.KEYWORDS)
                                .appendToResponse(AppendableRequest.Movie.LISTS)
                                .appendToResponse(AppendableRequest.Movie.RELEASES)
                                .appendToResponse(AppendableRequest.Movie.REVIEWS)
                                .appendToResponse(AppendableRequest.Movie.SIMILAR)
                                .appendToResponse(AppendableRequest.Movie.TRANSLATIONS)
                                .appendToResponse(AppendableRequest.Movie.VIDEOS)
                                .build()))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        MovieWithExtraDTO dto = response.body();
        assertThat(dto.getAdult()).isFalse();
        assertThat(dto.getBackdropPath()).isEqualTo("/wVTYlkKPKrljJfugXN7UlLNjtuJ.jpg");
        assertThat(dto.getGenreIds()).containsOnly(new GenreId(28), new GenreId(12), new GenreId(80));
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getOriginalLanguage()).isEqualTo(new Locale("en"));
        assertThat(dto.getOriginalTitle()).isEqualTo("Spectre");
        assertThat(dto.getOverview()).startsWith("A cryptic message from Bond’s");
        assertThat(dto.getReleaseDate()).isEqualTo(formatter.parse("2015-11-06"));
        assertThat(dto.getPosterPath()).isEqualTo("/1n9D32o30XOHMdMWuIT4AaA5ruI.jpg");
        assertThat(dto.getPopularity()).isGreaterThan(50f);
        assertThat(dto.getTitle()).isEqualTo("Spectre");
        assertThat(dto.getVideo()).isFalse();
        assertThat(dto.getVoteAverage()).isGreaterThan(5f);
        assertThat(dto.getVoteCount()).isGreaterThanOrEqualTo(453);
        // Alternative titles
        assertThat(dto.getAlternativeTitles().getTitles().size()).isGreaterThanOrEqualTo(6);
        assertThat(dto.getAlternativeTitles().getTitles().get(1).getIso3166Dash1()).isEqualTo(CountryCode.ES);
        assertThat(dto.getAlternativeTitles().getTitles().get(1).getTitle()).isEqualTo("007 James Bond: Spectre");
        // Credits
        assertThat(dto.getCredits().getCast().get(0).getCastId()).isEqualTo(new CastId(1));
        assertThat(dto.getCredits().getCast().get(0).getCharacter()).isEqualTo("James Bond");
        assertThat(dto.getCredits().getCast().get(0).getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d6b"));
        assertThat(dto.getCredits().getCast().get(0).getId()).isEqualTo(new PersonId(8784));
        assertThat(dto.getCredits().getCast().get(0).getName()).isEqualTo("Daniel Craig");
        assertThat(dto.getCredits().getCast().get(0).getOrder()).isEqualTo(0);
        assertThat(dto.getCredits().getCast().get(0).getProfilePath()).isEqualTo("/cO5OUQAMM6a4Rndw5Hc81KgpF5p.jpg");
        assertThat(dto.getCredits().getCrew().size()).isEqualTo(15);
        assertThat(dto.getCredits().getCrew().get(0).getCreditId()).isEqualTo(new CreditId("52fe4d22c3a368484e1d8d71"));
        assertThat(dto.getCredits().getCrew().get(0).getDepartment()).isEqualTo("Writing");
        assertThat(dto.getCredits().getCrew().get(0).getId()).isEqualTo(new PersonId(9856));
        assertThat(dto.getCredits().getCrew().get(0).getJob()).isEqualTo("Characters");
        assertThat(dto.getCredits().getCrew().get(0).getName()).isEqualTo("Ian Fleming");
        assertThat(dto.getCredits().getCrew().get(0).getProfilePath()).isEqualTo("/91U37Em6Ru87DiAPMdsocGKyQ0W.jpg");
        // Images
        assertThat(dto.getImages().getBackdrops().size()).isEqualTo(11);
        assertThat(dto.getImages().getBackdrops().get(8).getAspectRatio()).isEqualTo(1.77777777777778f, Offset.offset(0.001f));
        assertThat(dto.getImages().getBackdrops().get(8).getFilePath()).isEqualTo("/cNiO22mtARcyeNivzeON9sOvr65.jpg");
        assertThat(dto.getImages().getBackdrops().get(8).getHeight()).isEqualTo(720);
        assertThat(dto.getImages().getBackdrops().get(8).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getImages().getBackdrops().get(8).getVoteAverage()).isEqualTo(5.18601190476191f, Offset.offset(0.001f));
        assertThat(dto.getImages().getBackdrops().get(8).getVoteCount()).isEqualTo(1);
        assertThat(dto.getImages().getBackdrops().get(8).getWidth()).isEqualTo(1280);
        assertThat(dto.getImages().getPosters().size()).isEqualTo(26);
        assertThat(dto.getImages().getPosters().get(4).getAspectRatio()).isEqualTo(0.666666666666667f, Offset.offset(0.001f));
        assertThat(dto.getImages().getPosters().get(4).getFilePath()).isEqualTo("/1n9D32o30XOHMdMWuIT4AaA5ruI.jpg");
        assertThat(dto.getImages().getPosters().get(4).getHeight()).isEqualTo(3000);
        assertThat(dto.getImages().getPosters().get(4).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getImages().getPosters().get(4).getVoteAverage()).isEqualTo(5.31292517006803f, Offset.offset(0.001f));
        assertThat(dto.getImages().getPosters().get(4).getVoteCount()).isEqualTo(7);
        assertThat(dto.getImages().getPosters().get(4).getWidth()).isEqualTo(2000);
        // Keywords
        assertThat(dto.getKeywords().getKeywords().size()).isEqualTo(4);
        assertThat(dto.getKeywords().getKeywords().get(0).getId()).isEqualTo(new KeywordId(12360));
        assertThat(dto.getKeywords().getKeywords().get(0).getName()).isEqualTo("james bond");
        // Lists
        assertThat(dto.getLists().getPage()).isEqualTo(1);
        assertThat(dto.getLists().getResults().size()).isEqualTo(20);
        assertThat(dto.getLists().getResults().get(0).getDescription()).isEqualTo("James Bond");
        assertThat(dto.getLists().getResults().get(0).getFavoriteCount()).isGreaterThanOrEqualTo(5);
        assertThat(dto.getLists().getResults().get(0).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getLists().getResults().get(0).getItemCount()).isGreaterThanOrEqualTo(26);
        assertThat(dto.getLists().getResults().get(0).getId()).isEqualTo(new ListId("5308b87fc3a36842010027be"));
        assertThat(dto.getLists().getResults().get(0).getName()).isEqualTo("James Bond - Movie Collection");
        assertThat(dto.getLists().getResults().get(0).getPosterPath()).isEqualTo("/jHt3L6rxboCMHULYGdmv6TqjvZr.jpg");
        assertThat(dto.getLists().getTotalPages()).isGreaterThanOrEqualTo(2);
        assertThat(dto.getLists().getTotalResults()).isGreaterThanOrEqualTo(29);
        // Releases
        assertThat(dto.getReleases().getCountries().size()).isEqualTo(59);
        assertThat(dto.getReleases().getCountries().get(0).getCertification()).isEmpty();
        assertThat(dto.getReleases().getCountries().get(0).getIso3166Dash1()).isEqualTo(CountryCode.GB);
        assertThat(dto.getReleases().getCountries().get(0).isPrimary()).isFalse();
        assertThat(dto.getReleases().getCountries().get(0).getReleaseDate()).isEqualTo(formatter.parse("2015-10-26"));
        assertThat(dto.getReleases().getCountries().get(1).getCertification()).isEqualTo("PG-13");
        assertThat(dto.getReleases().getCountries().get(1).getIso3166Dash1()).isEqualTo(CountryCode.US);
        assertThat(dto.getReleases().getCountries().get(1).isPrimary()).isTrue();
        assertThat(dto.getReleases().getCountries().get(1).getReleaseDate()).isEqualTo(formatter.parse("2015-11-06"));
        // Reviews
        assertThat(dto.getReviews().getPage()).isEqualTo(1);
        assertThat(dto.getReviews().getResults().size()).isEqualTo(2);
        assertThat(dto.getReviews().getResults().get(0).getAuthor()).isEqualTo("cutprintchris");
        assertThat(dto.getReviews().getResults().get(0).getContent()).startsWith("<a href=\"http://www.cutprintfilm.com/r");
        assertThat(dto.getReviews().getResults().get(0).getId()).isEqualTo(new ReviewId("563e06159251413b1300c821"));
        assertThat(dto.getReviews().getResults().get(0).getUrl()).isEqualTo("http://j.mp/1MPodnZ");
        assertThat(dto.getReviews().getTotalPages()).isGreaterThanOrEqualTo(1);
        assertThat(dto.getReviews().getTotalResults()).isGreaterThanOrEqualTo(2);
        // Similar
        assertThat(dto.getSimilar().getPage()).isEqualTo(1);
        assertThat(dto.getSimilar().getResults().size()).isEqualTo(20);
        assertThat(dto.getSimilar().getResults().get(0).getAdult()).isEqualTo(false);
        assertThat(dto.getSimilar().getResults().get(0).getBackdropPath()).isEqualTo("/iyD72nJFBGbEIrpQjzdhE3wFxPL.jpg");
        assertThat(dto.getSimilar().getResults().get(0).getGenreIds()).containsOnly(
                new GenreId(12),
                new GenreId(28),
                new GenreId(53));
        assertThat(dto.getSimilar().getResults().get(0).getId()).isEqualTo(new MovieId(658));
        assertThat(dto.getSimilar().getResults().get(0).getOriginalLanguage()).isEqualTo(new Locale("en"));
        assertThat(dto.getSimilar().getResults().get(0).getOriginalTitle()).isEqualTo("Goldfinger");
        assertThat(dto.getSimilar().getResults().get(0).getOverview()).startsWith("Bond is in Miami on holiday when");
        assertThat(dto.getSimilar().getResults().get(0).getPosterPath()).isEqualTo("/vBNbFU3OS6okJIQBOos1aZXpy2Z.jpg");
        assertThat(dto.getSimilar().getResults().get(0).getPopularity()).isGreaterThanOrEqualTo(2);
        assertThat(dto.getSimilar().getResults().get(0).getReleaseDate()).isEqualTo(formatter.parse("1964-09-17"));
        assertThat(dto.getSimilar().getResults().get(0).getTitle()).isEqualTo("Goldfinger");
        assertThat(dto.getSimilar().getResults().get(0).getVideo()).isFalse();
        assertThat(dto.getSimilar().getResults().get(0).getVoteAverage()).isGreaterThanOrEqualTo(5);
        assertThat(dto.getSimilar().getResults().get(0).getVoteCount()).isGreaterThanOrEqualTo(356);
        assertThat(dto.getSimilar().getTotalPages()).isEqualTo(3);
        assertThat(dto.getSimilar().getTotalResults()).isEqualTo(44);
        // Translations
        assertThat(dto.getTranslations().getTranslations().size()).isEqualTo(22);
        assertThat(dto.getTranslations().getTranslations().get(1).getEnglishName()).isEqualTo("French");
        assertThat(dto.getTranslations().getTranslations().get(1).getIso639Dash1()).isEqualTo(new Locale("fr"));
        assertThat(dto.getTranslations().getTranslations().get(1).getName()).isEqualTo("Français");
        // Videos
        assertThat(dto.getVideos().getResults().size()).isEqualTo(3);
        assertThat(dto.getVideos().getResults().get(0).getId()).isEqualTo(new VideoId("5641eb2fc3a3685bdc002e0b"));
        assertThat(dto.getVideos().getResults().get(0).getIso639Dash1()).isEqualTo(new Locale("en"));
        assertThat(dto.getVideos().getResults().get(0).getKey()).isEqualTo("BOVriTeIypQ");
        assertThat(dto.getVideos().getResults().get(0).getName()).isEqualTo("Spectre Ultimate 007 Trailer 2015 HD");
        assertThat(dto.getVideos().getResults().get(0).getSite()).isEqualTo("YouTube");
        assertThat(dto.getVideos().getResults().get(0).getSize()).isEqualTo(1080);
        assertThat(dto.getVideos().getResults().get(0).getType()).isEqualTo("Trailer");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetLatestMovie() throws Exception
    {
        Response<MovieDTO> response = service
                .getLatestMovie()
                .execute();
        assertThat(response.isSuccess()).isTrue();
        MovieDTO dto = response.body();
        assertThat(dto.getId().getId()).isGreaterThanOrEqualTo(368669);
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetLatestMovieWithExtras() throws Exception
    {
        Response<MovieWithExtraDTO> response = service
                .getLatestMovie(new MovieRequestParameters.Builder()
                        .appendToResponse(AppendableRequest.Movie.IMAGES)
                        .build())
                .execute();
        assertThat(response.isSuccess()).isTrue();
        MovieWithExtraDTO dto = response.body();
        assertThat(dto.getId().getId()).isGreaterThanOrEqualTo(368669);
        assertThat(dto.getImages()).isNotNull();
    }
}
