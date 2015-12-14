package com.github.xavierlepretre.tmdb.model.movie;

import com.github.xavierlepretre.tmdb.model.AppendableRequest;
import com.github.xavierlepretre.tmdb.model.AppendableRequest.Movie;
import com.github.xavierlepretre.tmdb.model.AppendableRequestSet;
import com.neovisionaries.i18n.LanguageCode;

import org.junit.Test;

import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;

public class MovieRequestParametersBuilderTest
{
    @Test
    public void getValuesInBuildRequest() throws Exception
    {
        MovieRequestParameters request = new MovieRequestParameters.Builder()
                .language(LanguageCode.th)
                .appendToResponse(new AppendableRequestSet(Arrays.asList(
                        Movie.ALTERNATIVE_TITLES,
                        Movie.CREDITS)))
                .build();

        assertThat(request.getAppendToResponse()).containsOnly(
                Movie.ALTERNATIVE_TITLES,
                Movie.CREDITS);
        assertThat(request.getLanguage()).isEqualTo(LanguageCode.th);
    }

    @Test
    public void addLanguageStringWorksToo() throws Exception
    {
        MovieRequestParameters request = new MovieRequestParameters.Builder()
                .language("th")
                .build();

        assertThat(request.getLanguage()).isEqualTo(LanguageCode.th);
        assertThat(request.getAppendToResponse()).isEmpty();
    }

    @Test
    public void addAppendAddsToExisting() throws Exception
    {
        MovieRequestParameters request = new MovieRequestParameters.Builder()
                .appendToResponse(new AppendableRequestSet(Arrays.asList(
                        Movie.ALTERNATIVE_TITLES,
                        Movie.CREDITS)))
                .appendToResponse(Movie.IMAGES)
                .build();

        assertThat(request.getAppendToResponse()).containsOnly(
                Movie.ALTERNATIVE_TITLES,
                Movie.CREDITS,
                Movie.IMAGES);
    }

    @Test
    public void addThenAppendsCollectionAddsAll() throws Exception
    {
        MovieRequestParameters request = new MovieRequestParameters.Builder()
                .appendToResponse(Movie.IMAGES)
                .appendToResponse(new AppendableRequestSet(Arrays.asList(
                        Movie.ALTERNATIVE_TITLES,
                        Movie.CREDITS)))
                .build();

        assertThat(request.getAppendToResponse()).containsOnly(
                Movie.ALTERNATIVE_TITLES,
                Movie.CREDITS,
                Movie.IMAGES);
    }

    @Test
    public void addThenAppendsArrayAddsAll() throws Exception
    {
        MovieRequestParameters request = new MovieRequestParameters.Builder()
                .appendToResponse(Movie.IMAGES)
                .appendToResponse(new AppendableRequest[]{
                        Movie.ALTERNATIVE_TITLES,
                        Movie.CREDITS})
                .build();

        assertThat(request.getAppendToResponse()).containsOnly(
                Movie.ALTERNATIVE_TITLES,
                Movie.CREDITS,
                Movie.IMAGES);
    }
}