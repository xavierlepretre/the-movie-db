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
    public void getMovieAlternativeTitlesIsCalled() throws Exception
    {
        tmdbService.getMovieAlternativeTitles(new MovieId(98L));
        verify(tmdbRetrofit).getMovieAlternativeTitles(
                eq(98L),
                eq("some_key"));
    }

    @Test
    public void getMovieCreditsIsCalled() throws Exception
    {
        tmdbService.getMovieCredits(new MovieId(98L));
        verify(tmdbRetrofit).getMovieCredits(
                eq(98L),
                eq("some_key"));
    }

    @Test
    public void getMovieImagesIsCalled() throws Exception
    {
        tmdbService.getMovieImages(new MovieId(98L));
        verify(tmdbRetrofit).getMovieImages(
                eq(98L),
                eq("some_key"));
    }

    @Test
    public void getMovieKeywordsIsCalled() throws Exception
    {
        tmdbService.getMovieKeywords(new MovieId(98L));
        verify(tmdbRetrofit).getMovieKeywords(
                eq(98L),
                eq("some_key"));
    }

    @Test
    public void getMovieReleasesIsCalled() throws Exception
    {
        tmdbService.getMovieReleases(new MovieId(98L));
        verify(tmdbRetrofit).getMovieReleases(
                eq(98L),
                eq("some_key"));
    }

    @Test
    public void getMovieTranslationsIsCalled() throws Exception
    {
        tmdbService.getMovieTranslations(new MovieId(98L));
        verify(tmdbRetrofit).getMovieTranslations(
                eq(98L),
                eq("some_key"));
    }

    @Test
    public void getMovieVideosIsCalled() throws Exception
    {
        tmdbService.getMovieVideos(new MovieId(98L));
        verify(tmdbRetrofit).getMovieVideos(
                eq(98L),
                eq("some_key"));
    }
}
