package com.github.xavierlepretre.tmdb.net;

import android.support.test.runner.AndroidJUnit4;
import android.test.FlakyTest;

import com.github.xavierlepretre.tmdb.helper.IsConnectedTestRule;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.discover.DiscoverMoviesDTO;
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
public class TmdbServiceRequestTest
{
    private TmdbService service;

    @Rule
    public IsConnectedTestRule isConnectedTestRule = new IsConnectedTestRule();

    @Before
    public void setUp()
    {
        TmdbRetrofit retrofit = new TmdbRetrofitFactory().create();
        this.service = new TmdbService(retrofit, BuildConfig.TMDB_API_KEY);
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
        assertThat(dto.getGenreIds()).isEqualTo(Arrays.asList(new GenreId(28), new GenreId(12), new GenreId(80)));
        assertThat(dto.getId()).isEqualTo(new MovieId(206647));
        assertThat(dto.getOriginalLanguage()).isEqualTo(new Locale("en"));
        assertThat(dto.getOriginalTitle()).isEqualTo("Spectre");
        assertThat(dto.getOverview()).startsWith("A cryptic message from Bond’s");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
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
        Response<AlternativeTitlesDTO> response = service
                .getMovieAlternativeTitles(new MovieId(206647))
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
    public void canGetReleases() throws Exception
    {
        Response<ReleasesWithIdDTO> response = service
                .getMovieReleases(new MovieId(206647))
                .execute();
        assertThat(response.isSuccess()).isTrue();
        ReleasesWithIdDTO dto = response.body();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
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
}
