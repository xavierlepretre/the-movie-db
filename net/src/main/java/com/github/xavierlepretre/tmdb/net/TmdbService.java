package com.github.xavierlepretre.tmdb.net;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.discover.DiscoverMoviesDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.github.xavierlepretre.tmdb.model.movie.MovieRequest;

import java.util.Locale;

import retrofit.Call;

public class TmdbService
{
    @NonNull private final TmdbRetrofit tmdbRetrofit;

    public TmdbService(@NonNull TmdbRetrofit tmdbRetrofit)
    {
        this.tmdbRetrofit = tmdbRetrofit;
    }

    @NonNull Call<ConfigurationDTO> getConfiguration()
    {
        return tmdbRetrofit.getConfiguration(BuildConfig.TMDB_API_KEY);
    }

    @NonNull Call<DiscoverMoviesDTO> discoverMovies()
    {
        return tmdbRetrofit.discoverMovies(BuildConfig.TMDB_API_KEY);
    }

    @NonNull Call<MovieDTO> getMovie(@NonNull MovieId movieId)
    {
        return getMovie(new MovieRequest(movieId));
    }

    @NonNull Call<MovieDTO> getMovie(@NonNull MovieRequest movieRequest)
    {
        Locale language = movieRequest.getLanguage();
        return tmdbRetrofit.getMovie(
                movieRequest.getMovieId().getId(),
                BuildConfig.TMDB_API_KEY,
                language == null ? null : language.getLanguage(),
                movieRequest.getAppendToResponse().toString());
    }

}
