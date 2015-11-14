package com.github.xavierlepretre.tmdb.model.movie;

import com.github.xavierlepretre.tmdb.model.AppendableRequest;
import com.github.xavierlepretre.tmdb.model.AppendableRequest.Movie;
import com.github.xavierlepretre.tmdb.model.AppendableRequestSet;

import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class MovieRequestBuilderTest
{
    @Test
    public void getValuesInBuildRequest() throws Exception
    {
        MovieRequest request = new MovieRequest.Builder(new MovieId(22))
                .language(new Locale("th"))
                .appendToResponse(new AppendableRequestSet(Arrays.asList(
                        Movie.ALTERNATIVE_TITLES,
                        Movie.CREDITS)))
                .build();

        assertThat(request.getMovieId()).isEqualTo(new MovieId(22));
        assertThat(request.getAppendToResponse()).isEqualTo(new AppendableRequestSet(Arrays.asList(
                Movie.ALTERNATIVE_TITLES,
                Movie.CREDITS)));
        assertThat(request.getLanguage()).isEqualTo(new Locale("th"));
    }

    @Test
    public void addLanguageStringWorksToo() throws Exception
    {
        MovieRequest request = new MovieRequest.Builder(new MovieId(22))
                .language("th")
                .build();

        assertThat(request.getMovieId()).isEqualTo(new MovieId(22));
        assertThat(request.getLanguage()).isEqualTo(new Locale("th"));
    }

    @Test
    public void addAppendAddsToExisting() throws Exception
    {
        MovieRequest request = new MovieRequest.Builder(new MovieId(22))
                .appendToResponse(new AppendableRequestSet(Arrays.asList(
                        Movie.ALTERNATIVE_TITLES,
                        Movie.CREDITS)))
                .appendToResponse(Movie.IMAGES)
                .build();

        assertThat(request.getMovieId()).isEqualTo(new MovieId(22));
        assertThat(request.getAppendToResponse()).isEqualTo(new AppendableRequestSet(Arrays.asList(
                Movie.ALTERNATIVE_TITLES,
                Movie.CREDITS,
                Movie.IMAGES)));
    }

    @Test
    public void addThenAppendsCollectionAddsAll() throws Exception
    {
        MovieRequest request = new MovieRequest.Builder(new MovieId(22))
                .appendToResponse(Movie.IMAGES)
                .appendToResponse(new AppendableRequestSet(Arrays.asList(
                        Movie.ALTERNATIVE_TITLES,
                        Movie.CREDITS)))
                .build();

        assertThat(request.getMovieId()).isEqualTo(new MovieId(22));
        assertThat(request.getAppendToResponse()).isEqualTo(new AppendableRequestSet(Arrays.asList(
                Movie.ALTERNATIVE_TITLES,
                Movie.CREDITS,
                Movie.IMAGES)));
    }

    @Test
    public void addThenAppendsArrayAddsAll() throws Exception
    {
        MovieRequest request = new MovieRequest.Builder(new MovieId(22))
                .appendToResponse(Movie.IMAGES)
                .appendToResponse(new AppendableRequest[]{
                        Movie.ALTERNATIVE_TITLES,
                        Movie.CREDITS})
                .build();

        assertThat(request.getMovieId()).isEqualTo(new MovieId(22));
        assertThat(request.getAppendToResponse()).isEqualTo(new AppendableRequestSet(Arrays.asList(
                Movie.ALTERNATIVE_TITLES,
                Movie.CREDITS,
                Movie.IMAGES)));
    }
}