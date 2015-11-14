package com.github.xavierlepretre.tmdb.net;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.discover.DiscoverMoviesDTO;
import com.github.xavierlepretre.tmdb.model.image.ImagesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.movie.AlternativeTitlesDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.github.xavierlepretre.tmdb.model.movie.MovieRequest;
import com.github.xavierlepretre.tmdb.model.people.CreditsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.show.ReleasesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.tag.KeywordsWithIdDTO;

import java.util.Locale;

import retrofit.Call;

public class TmdbService
{
    @NonNull private final TmdbRetrofit tmdbRetrofit;

    public TmdbService(@NonNull TmdbRetrofit tmdbRetrofit)
    {
        this.tmdbRetrofit = tmdbRetrofit;
    }

    @NonNull public Call<ConfigurationDTO> getConfiguration()
    {
        return tmdbRetrofit.getConfiguration(BuildConfig.TMDB_API_KEY);
    }

    @NonNull public Call<DiscoverMoviesDTO> discoverMovies()
    {
        return tmdbRetrofit.discoverMovies(BuildConfig.TMDB_API_KEY);
    }

    @NonNull public Call<MovieDTO> getMovie(@NonNull MovieId movieId)
    {
        return getMovie(new MovieRequest(movieId));
    }

    @NonNull public Call<MovieDTO> getMovie(@NonNull MovieRequest movieRequest)
    {
        Locale language = movieRequest.getLanguage();
        return tmdbRetrofit.getMovie(
                movieRequest.getMovieId().getId(),
                BuildConfig.TMDB_API_KEY,
                language == null ? null : language.getLanguage(),
                movieRequest.getAppendToResponse().toString());
    }

    @NonNull public Call<AlternativeTitlesDTO> getMovieAlternativeTitles(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieAlternativeTitles(
                movieId.getId(),
                BuildConfig.TMDB_API_KEY);
    }

    @NonNull public Call<CreditsWithIdDTO> getMovieCredits(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieCredits(
                movieId.getId(),
                BuildConfig.TMDB_API_KEY);
    }

    @NonNull public Call<ImagesWithIdDTO> getMovieImages(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieImages(
                movieId.getId(),
                BuildConfig.TMDB_API_KEY);
    }

    @NonNull public Call<KeywordsWithIdDTO> getMovieKeywords(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieKeywords(
                movieId.getId(),
                BuildConfig.TMDB_API_KEY);
    }

    @NonNull public Call<ReleasesWithIdDTO> getMovieReleases(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieReleases(
                movieId.getId(),
                BuildConfig.TMDB_API_KEY);
    }
}
