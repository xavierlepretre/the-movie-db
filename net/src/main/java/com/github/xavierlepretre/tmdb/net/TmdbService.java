package com.github.xavierlepretre.tmdb.net;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.discover.DiscoverMoviesDTO;
import com.github.xavierlepretre.tmdb.model.i18n.TranslationsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.image.ImagesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.movie.AlternativeTitlesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;
import com.github.xavierlepretre.tmdb.model.movie.MovieRequest;
import com.github.xavierlepretre.tmdb.model.movie.MovieWithExtraDTO;
import com.github.xavierlepretre.tmdb.model.people.CreditsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.rate.ReviewsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.show.ReleasesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.show.VideosWithIdDTO;
import com.github.xavierlepretre.tmdb.model.tag.KeywordsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.tag.ListsWithIdDTO;

import java.util.Locale;

import retrofit.Call;

public class TmdbService
{
    @NonNull private final TmdbRetrofit tmdbRetrofit;
    @NonNull private final String apiKey;

    public TmdbService(@NonNull TmdbRetrofit tmdbRetrofit, @NonNull String apiKey)
    {
        this.tmdbRetrofit = tmdbRetrofit;
        this.apiKey = apiKey;
    }

    @NonNull public Call<ConfigurationDTO> getConfiguration()
    {
        return tmdbRetrofit.getConfiguration(apiKey);
    }

    @NonNull public Call<DiscoverMoviesDTO> discoverMovies()
    {
        return tmdbRetrofit.discoverMovies(apiKey);
    }

    @NonNull public Call<MovieDTO> getMovie(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovie(movieId.getId(), apiKey);
    }

    @NonNull public Call<MovieWithExtraDTO> getMovie(@NonNull MovieRequest movieRequest)
    {
        Locale language = movieRequest.getLanguage();
        return tmdbRetrofit.getMovie(
                movieRequest.getMovieId().getId(),
                apiKey,
                language == null ? null : language.getLanguage(),
                movieRequest.getAppendToResponse().toString());
    }

    @NonNull public Call<AlternativeTitlesWithIdDTO> getMovieAlternativeTitles(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieAlternativeTitles(
                movieId.getId(),
                apiKey);
    }

    @NonNull public Call<CreditsWithIdDTO> getMovieCredits(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieCredits(
                movieId.getId(),
                apiKey);
    }

    @NonNull public Call<ImagesWithIdDTO> getMovieImages(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieImages(
                movieId.getId(),
                apiKey);
    }

    @NonNull public Call<KeywordsWithIdDTO> getMovieKeywords(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieKeywords(
                movieId.getId(),
                apiKey);
    }

    @NonNull public Call<ListsWithIdDTO> getMovieLists(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieLists(
                movieId.getId(),
                apiKey);
    }

    @NonNull public Call<ReleasesWithIdDTO> getMovieReleases(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieReleases(
                movieId.getId(),
                apiKey);
    }

    @NonNull public Call<ReviewsWithIdDTO> getMovieReviews(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieReviews(
                movieId.getId(),
                apiKey);
    }

    @NonNull public Call<DiscoverMoviesDTO> getMovieSimilar(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieSimilar(
                movieId.getId(),
                apiKey);
    }

    @NonNull public Call<TranslationsWithIdDTO> getMovieTranslations(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieTranslations(
                movieId.getId(),
                apiKey);
    }

    @NonNull public Call<VideosWithIdDTO> getMovieVideos(@NonNull MovieId movieId)
    {
        return tmdbRetrofit.getMovieVideos(
                movieId.getId(),
                apiKey);
    }
}
