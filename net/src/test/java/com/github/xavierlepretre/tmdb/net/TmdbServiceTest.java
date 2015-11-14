package com.github.xavierlepretre.tmdb.net;

import com.github.xavierlepretre.tmdb.model.AppendableRequestFactory;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.github.xavierlepretre.tmdb.model.movie.MovieRequest;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TmdbServiceTest
{
    private TmdbRetrofit tmdbRetrofit;
    private TmdbService tmdbService;

    @Before
    public void setUp()
    {
        this.tmdbRetrofit = mock(TmdbRetrofit.class);
        this.tmdbService = new TmdbService(tmdbRetrofit, "some_key");
    }

    @Test
    public void configurationIsCalled() throws Exception
    {
        tmdbService.getConfiguration();
        verify(tmdbRetrofit).getConfiguration(eq("some_key"));
    }

    @Test
    public void discoverMoviesIsCalled() throws Exception
    {
        tmdbService.discoverMovies();
        verify(tmdbRetrofit).discoverMovies(eq("some_key"));
    }

    @Test
    public void getMovieIsCalled() throws Exception
    {
        MovieRequest movieRequest = new MovieRequest.Builder(new MovieId(22))
                .language("de")
                .appendToResponse(new AppendableRequestFactory().create("a_call"))
                .appendToResponse(new AppendableRequestFactory().create("other_call"))
                .build();
        tmdbService.getMovie(movieRequest);
        verify(tmdbRetrofit).getMovie(
                eq(22L),
                eq("some_key"),
                eq("de"),
                eq("a_call,other_call"));
    }

    @Test
    public void getAlternativeMoviesIsCalled() throws Exception
    {
        tmdbService.getMovieAlternativeTitles(new MovieId(98L));
        verify(tmdbRetrofit).getMovieAlternativeTitles(
                eq(98L),
                eq("some_key"));
    }
}
