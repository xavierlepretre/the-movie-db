package com.github.xavierlepretre.tmdb.net;

import android.support.test.runner.AndroidJUnit4;
import android.test.FlakyTest;

import com.github.xavierlepretre.tmdb.helper.IsConnectedTestRule;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.discover.DiscoverMoviesDTO;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;
import com.github.xavierlepretre.tmdb.model.movie.MovieDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

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
        this.service = new TmdbService(retrofit);
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
}
