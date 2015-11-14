package com.github.xavierlepretre.tmdb.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.discover.DiscoverMoviesDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieDTO;
import com.github.xavierlepretre.tmdb.net.TmdbConstants.Configuration;
import com.github.xavierlepretre.tmdb.net.TmdbConstants.Discover;
import com.github.xavierlepretre.tmdb.net.TmdbConstants.Movie;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface TmdbRetrofit
{
    @GET(Configuration.PATH_CONFIGURATION)
    @NonNull Call<ConfigurationDTO> getConfiguration(
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);

    @GET(Discover.PATH_DISCOVER_MOVIE)
    @NonNull Call<DiscoverMoviesDTO> discoverMovies(
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);

    @GET(Movie.PATH_MOVIE + "/{movieId}")
    @NonNull Call<MovieDTO> getMovie(
            @Path("movieId") long movieId,
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey,
            @Query(TmdbConstants.QUERY_LANGUAGE) @Nullable String language,
            @Query(TmdbConstants.QUERY_APPEND_TO_RESPONSE) @Nullable String appendToResponse);
}
