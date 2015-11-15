package com.github.xavierlepretre.tmdb.net;

import android.support.test.runner.AndroidJUnit4;
import android.test.FlakyTest;

import com.github.xavierlepretre.tmdb.helper.IsConnectedTestRule;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.discover.DiscoverMoviesDTO;
import com.github.xavierlepretre.tmdb.model.i18n.TranslationsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.image.ImagesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.movie.AlternativeTitlesDTO;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;
import com.github.xavierlepretre.tmdb.model.movie.MovieDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.github.xavierlepretre.tmdb.model.people.CastId;
import com.github.xavierlepretre.tmdb.model.people.CreditId;
import com.github.xavierlepretre.tmdb.model.people.CreditsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.people.PersonId;
import com.github.xavierlepretre.tmdb.model.show.ReleasesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.show.VideoId;
import com.github.xavierlepretre.tmdb.model.show.VideosWithIdDTO;
import com.github.xavierlepretre.tmdb.model.tag.KeywordId;
import com.github.xavierlepretre.tmdb.model.tag.KeywordsWithIdDTO;
import com.neovisionaries.i18n.CountryCode;

import org.fest.assertions.data.Offset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.Response;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class TmdbRetrofitRequestTest
{
    private TmdbRetrofit retrofit;
    private DateFormat formatter;

    @Rule
    public IsConnectedTestRule isConnectedTestRule = new IsConnectedTestRule();

    @Before
    public void setUp()
    {
        this.retrofit = new TmdbRetrofitFactory().create();
        this.formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetConfiguration() throws Exception
    {
        Response<ConfigurationDTO> response = retrofit
                .getConfiguration(BuildConfig.TMDB_API_KEY)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.body().getImagesConf().getBaseUrl()).isEqualTo("http://image.tmdb.org/t/p/");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canDiscoverMovies() throws Exception
    {
        Response<DiscoverMoviesDTO> response = retrofit
                .discoverMovies(BuildConfig.TMDB_API_KEY)
                .execute();
        assertThat(response.isSuccess()).isTrue();
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetMovie() throws Exception
    {
        Response<MovieDTO> response = retrofit
                .getMovie(206647, BuildConfig.TMDB_API_KEY, null, null)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        MovieDTO dto = response.body();
        assertThat(dto.getAdult()).isFalse();
        assertThat(dto.getBackdropPath()).isEqualTo("/wVTYlkKPKrljJfugXN7UlLNjtuJ.jpg");
        assertThat(dto.getGenreIds()).isEqualTo(Arrays.asList(new GenreId(28), new GenreId(12), new GenreId(80)));
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
        Response<AlternativeTitlesDTO> response = retrofit
                .getMovieAlternativeTitles(206647, BuildConfig.TMDB_API_KEY)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        AlternativeTitlesDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getTitles().size()).isGreaterThanOrEqualTo(6);
        assertThat(dto.getTitles().get(1).getIso3166Dash1()).isEqualTo(CountryCode.ES);
        assertThat(dto.getTitles().get(1).getTitle()).isEqualTo("007 James Bond: Spectre");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetCredits() throws Exception
    {
        Response<CreditsWithIdDTO> response = retrofit
                .getMovieCredits(206647, BuildConfig.TMDB_API_KEY)
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
        Response<ImagesWithIdDTO> response = retrofit
                .getMovieImages(206647, BuildConfig.TMDB_API_KEY)
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
        Response<KeywordsWithIdDTO> response = retrofit
                .getMovieKeywords(206647, BuildConfig.TMDB_API_KEY)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        KeywordsWithIdDTO dto = response.body();
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getKeywords().size()).isEqualTo(4);
        assertThat(dto.getKeywords().get(0).getId()).isEqualTo(new KeywordId(12360));
        assertThat(dto.getKeywords().get(0).getName()).isEqualTo("james bond");
    }

    @Test @FlakyTest(tolerance = 3)
    public void canGetReleases() throws Exception
    {
        Response<ReleasesWithIdDTO> response = retrofit
                .getMovieReleases(206647, BuildConfig.TMDB_API_KEY)
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
    public void canGetSimilar() throws Exception
    {
        Response<DiscoverMoviesDTO> response = retrofit
                .getMovieSimilar(206647, BuildConfig.TMDB_API_KEY)
                .execute();
        assertThat(response.isSuccess()).isTrue();
        DiscoverMoviesDTO dto = response.body();
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getResults().size()).isEqualTo(20);
        assertThat(dto.getResults().get(0).getAdult()).isEqualTo(false);
        assertThat(dto.getResults().get(0).getBackdropPath()).isEqualTo("/iyD72nJFBGbEIrpQjzdhE3wFxPL.jpg");
        assertThat(dto.getResults().get(0).getGenreIds()).isEqualTo(Arrays.asList(
                new GenreId(12),
                new GenreId(28),
                new GenreId(53)));
        assertThat(dto.getResults().get(0).getId()).isEqualTo(new MovieId(658));
        assertThat(dto.getResults().get(0).getOriginalLanguage()).isEqualTo(new Locale("en"));
        assertThat(dto.getResults().get(0).getOriginalTitle()).isEqualTo("Goldfinger");
        assertThat(dto.getResults().get(0).getOverview()).startsWith("Bond is in Miami on holiday when");
        assertThat(dto.getResults().get(0).getPosterPath()).isEqualTo("/vBNbFU3OS6okJIQBOos1aZXpy2Z.jpg");
        assertThat(dto.getResults().get(0).getPopularity()).isGreaterThanOrEqualTo(3);
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
        Response<TranslationsWithIdDTO> response = retrofit
                .getMovieTranslations(206647, BuildConfig.TMDB_API_KEY)
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
        Response<VideosWithIdDTO> response = retrofit
                .getMovieVideos(206647, BuildConfig.TMDB_API_KEY)
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
}
