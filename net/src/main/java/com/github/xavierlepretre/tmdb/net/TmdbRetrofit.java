package com.github.xavierlepretre.tmdb.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.TmdbDtoConstants;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationDTO;
import com.github.xavierlepretre.tmdb.model.discover.DiscoverMoviesDTO;
import com.github.xavierlepretre.tmdb.model.i18n.TranslationsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.image.ImagesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.movie.AlternativeTitlesDTO;
import com.github.xavierlepretre.tmdb.model.movie.MovieDTO;
import com.github.xavierlepretre.tmdb.model.people.CreditsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.rate.ReviewsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.show.ReleasesWithIdDTO;
import com.github.xavierlepretre.tmdb.model.show.VideosWithIdDTO;
import com.github.xavierlepretre.tmdb.model.tag.KeywordsWithIdDTO;
import com.github.xavierlepretre.tmdb.model.tag.ListsWithIdDTO;
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

    @GET(Movie.PATH_MOVIE + "/{movieId}/" + TmdbDtoConstants.Movie.EXTRA_ALTERNATIVE_TITLES)
    @NonNull Call<AlternativeTitlesDTO> getMovieAlternativeTitles(
            @Path("movieId") long movieId,
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);

    @GET(Movie.PATH_MOVIE + "/{movieId}/" + TmdbDtoConstants.Movie.EXTRA_CREDITS)
    @NonNull Call<CreditsWithIdDTO> getMovieCredits(
            @Path("movieId") long movieId,
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);

    @GET(Movie.PATH_MOVIE + "/{movieId}/" + TmdbDtoConstants.Movie.EXTRA_IMAGES)
    @NonNull Call<ImagesWithIdDTO> getMovieImages(
            @Path("movieId") long movieId,
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);

    @GET(Movie.PATH_MOVIE + "/{movieId}/" + TmdbDtoConstants.Movie.EXTRA_KEYWORDS)
    @NonNull Call<KeywordsWithIdDTO> getMovieKeywords(
            @Path("movieId") long movieId,
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);

    @GET(Movie.PATH_MOVIE + "/{movieId}/" + TmdbDtoConstants.Movie.EXTRA_LISTS)
    @NonNull Call<ListsWithIdDTO> getMovieLists(
            @Path("movieId") long movieId,
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);

    @GET(Movie.PATH_MOVIE + "/{movieId}/" + TmdbDtoConstants.Movie.EXTRA_RELEASES)
    @NonNull Call<ReleasesWithIdDTO> getMovieReleases(
            @Path("movieId") long movieId,
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);

    @GET(Movie.PATH_MOVIE + "/{movieId}/" + TmdbDtoConstants.Movie.EXTRA_REVIEWS)
    @NonNull Call<ReviewsWithIdDTO> getMovieReviews(
            @Path("movieId") long movieId,
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);

    @GET(Movie.PATH_MOVIE + "/{movieId}/" + TmdbDtoConstants.Movie.EXTRA_SIMILAR)
    @NonNull Call<DiscoverMoviesDTO> getMovieSimilar(
            @Path("movieId") long movieId,
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);

    @GET(Movie.PATH_MOVIE + "/{movieId}/" + TmdbDtoConstants.Movie.EXTRA_TRANSLATIONS)
    @NonNull Call<TranslationsWithIdDTO> getMovieTranslations(
            @Path("movieId") long movieId,
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);

    @GET(Movie.PATH_MOVIE + "/{movieId}/" + TmdbDtoConstants.Movie.EXTRA_VIDEOS)
    @NonNull Call<VideosWithIdDTO> getMovieVideos(
            @Path("movieId") long movieId,
            @Query(TmdbConstants.QUERY_API_KEY) @NonNull String apiKey);
}
