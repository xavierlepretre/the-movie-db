package com.github.xavierlepretre.tmdb.net;

import com.github.xavierlepretre.tmdb.model.AppendableRequestFactory;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.github.xavierlepretre.tmdb.model.movie.MovieRequest;
import com.github.xavierlepretre.tmdb.model.movie.MovieRequestParameters;

import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

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
        tmdbService.getMovie(new MovieId(22));
        verify(tmdbRetrofit).getMovie(
                eq(22L),
                eq("some_key"));
    }

    @Test
    public void getMovieWithExtraIsCalled() throws Exception
    {
        MovieRequestParameters parameters = new MovieRequestParameters.Builder()
                .language("de")
                .appendToResponse(new AppendableRequestFactory().create("a_call"))
                .appendToResponse(new AppendableRequestFactory().create("other_call"))
                .build();
        tmdbService.getMovie(new MovieRequest(new MovieId(22), parameters));
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
    public void getMovieListsIsCalled() throws Exception
    {
        tmdbService.getMovieLists(new MovieId(98L));
        verify(tmdbRetrofit).getMovieLists(
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
    public void getMovieSimilarIsCalled() throws Exception
    {
        tmdbService.getMovieSimilar(new MovieId(98L));
        verify(tmdbRetrofit).getMovieSimilar(
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

    @Test
    public void getLatestMovieIsCalled() throws Exception
    {
        tmdbService.getLatestMovie();
        verify(tmdbRetrofit).getLatestMovie(
                eq("some_key"));
    }

    @Test
    public void getLatestMovieWithExtraIsCalled() throws Exception
    {
        MovieRequestParameters parameters = new MovieRequestParameters.Builder()
                .language("de")
                .appendToResponse(new AppendableRequestFactory().create("a_call"))
                .appendToResponse(new AppendableRequestFactory().create("other_call"))
                .build();
        tmdbService.getLatestMovie(parameters);
        verify(tmdbRetrofit).getLatestMovie(
                eq("some_key"),
                eq("de"),
                eq("a_call,other_call"));
    }

    @Test
    public void getMovieGenreListIsCalled() throws Exception
    {
        tmdbService.getMovieGenreList(new Locale("hr"));
        verify(tmdbRetrofit).getMovieGenreList(
                eq("some_key"),
                eq("hr"));
    }
}
