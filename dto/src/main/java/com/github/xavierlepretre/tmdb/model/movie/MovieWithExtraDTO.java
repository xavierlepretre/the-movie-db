package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.TmdbDtoConstants.Movie;
import com.github.xavierlepretre.tmdb.model.discover.DiscoverMoviesDTO;
import com.github.xavierlepretre.tmdb.model.i18n.SpokenLanguageDTO;
import com.github.xavierlepretre.tmdb.model.i18n.TranslationsDTO;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.github.xavierlepretre.tmdb.model.image.ImagesDTO;
import com.github.xavierlepretre.tmdb.model.people.CreditsDTO;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyDTO;
import com.github.xavierlepretre.tmdb.model.production.ProductionCountryDTO;
import com.github.xavierlepretre.tmdb.model.rate.ReviewsDTO;
import com.github.xavierlepretre.tmdb.model.show.ReleasesDTO;
import com.github.xavierlepretre.tmdb.model.show.VideosDTO;
import com.github.xavierlepretre.tmdb.model.tag.KeywordsDTO;
import com.github.xavierlepretre.tmdb.model.tag.ListsDTO;
import com.neovisionaries.i18n.LanguageCode;

import java.util.Date;
import java.util.List;

public class MovieWithExtraDTO extends MovieDTO
{
    @Nullable private final AlternativeTitlesDTO alternativeTitles;
    @Nullable private final CreditsDTO credits;
    @Nullable private final ImagesDTO images;
    @Nullable private final KeywordsDTO keywords;
    @Nullable private final ListsDTO lists;
    @Nullable private final ReleasesDTO releases;
    @Nullable private final ReviewsDTO reviews;
    @Nullable private final DiscoverMoviesDTO similar;
    @Nullable private final TranslationsDTO translations;
    @Nullable private final VideosDTO videos;

    public MovieWithExtraDTO(
            @JsonProperty(value = "adult", required = true)
            boolean adult,
            @JsonProperty(Movie.EXTRA_ALTERNATIVE_TITLES) @Nullable
            AlternativeTitlesDTO alternativeTitles,
            @JsonProperty(value = "backdrop_path", required = true) @NonNull
            ImagePath backdropPath,
            @JsonProperty(value = "belongs_to_collection", required = true) @NonNull
            CollectionDTO belongsToCollection,
            @JsonProperty(value = "budget", required = true)
            long budget,
            @JsonProperty(Movie.EXTRA_CREDITS) @Nullable
            CreditsDTO credits,
            @JsonProperty(value = "genres", required = true) @NonNull
            List<GenreDTO> genres,
            @JsonProperty(value = "homepage", required = true) @NonNull
            String homepage,
            @JsonProperty(value = "id", required = true) @NonNull
            MovieId id,
            @JsonProperty(Movie.EXTRA_IMAGES) @Nullable
            ImagesDTO images,
            @JsonProperty(value = "imdb_id", required = true) @NonNull
            ImdbId imdbId,
            @JsonProperty(Movie.EXTRA_KEYWORDS) @Nullable
            KeywordsDTO keywords,
            @JsonProperty(Movie.EXTRA_LISTS) @Nullable
            ListsDTO lists,
            @JsonProperty(value = "original_language", required = true) @NonNull
            LanguageCode originalLanguage,
            @JsonProperty(value = "original_title", required = true) @NonNull
            String originalTitle,
            @JsonProperty(value = "overview", required = true) @NonNull
            String overview,
            @JsonProperty(value = "popularity", required = true)
            float popularity,
            @JsonProperty(value = "poster_path", required = true) @NonNull
            ImagePath posterPath,
            @JsonProperty(value = "production_companies", required = true) @NonNull
            List<ProductionCompanyDTO> productionCompanies,
            @JsonProperty(value = "production_countries", required = true) @NonNull
            List<ProductionCountryDTO> productionCountries,
            @JsonProperty(value = "release_date", required = true) @NonNull
            Date releaseDate,
            @JsonProperty(Movie.EXTRA_RELEASES) @Nullable
            ReleasesDTO releases,
            @JsonProperty(value = "revenue", required = true)
            long revenue,
            @JsonProperty(Movie.EXTRA_REVIEWS) @Nullable
            ReviewsDTO reviews,
            @JsonProperty(value = "runtime", required = true)
            int runtime,
            @JsonProperty(Movie.EXTRA_SIMILAR) @Nullable
            DiscoverMoviesDTO similar,
            @JsonProperty(value = "spoken_languages", required = true) @NonNull
            List<SpokenLanguageDTO> spokenLanguages,
            @JsonProperty(value = "status", required = true) @NonNull
            String status,
            @JsonProperty(value = "tagline", required = true) @NonNull
            String tagline,
            @JsonProperty(value = "title", required = true) @NonNull
            String title,
            @JsonProperty(Movie.EXTRA_TRANSLATIONS) @Nullable
            TranslationsDTO translations,
            @JsonProperty(value = "video", required = true)
            boolean video,
            @JsonProperty(Movie.EXTRA_VIDEOS) @Nullable
            VideosDTO videos,
            @JsonProperty(value = "vote_average", required = true)
            float voteAverage,
            @JsonProperty(value = "vote_count", required = true) @NonNull
            Integer voteCount)
    {
        super(adult,
                backdropPath,
                belongsToCollection,
                budget,
                genres,
                homepage,
                id,
                imdbId,
                originalLanguage,
                originalTitle,
                overview,
                popularity,
                posterPath,
                productionCompanies,
                productionCountries,
                releaseDate,
                revenue,
                runtime,
                spokenLanguages,
                status,
                tagline,
                title,
                video,
                voteAverage,
                voteCount);
        this.alternativeTitles = alternativeTitles;
        this.credits = credits;
        this.images = images;
        this.keywords = keywords;
        this.lists = lists;
        this.releases = releases;
        this.reviews = reviews;
        this.similar = similar;
        this.translations = translations;
        this.videos = videos;
    }

    @Nullable public AlternativeTitlesDTO getAlternativeTitles()
    {
        return alternativeTitles;
    }

    @Nullable public CreditsDTO getCredits()
    {
        return credits;
    }

    @Nullable public ImagesDTO getImages()
    {
        return images;
    }

    @Nullable public KeywordsDTO getKeywords()
    {
        return keywords;
    }

    @Nullable public ListsDTO getLists()
    {
        return lists;
    }

    @Nullable public ReleasesDTO getReleases()
    {
        return releases;
    }

    @Nullable public ReviewsDTO getReviews()
    {
        return reviews;
    }

    @Nullable public DiscoverMoviesDTO getSimilar()
    {
        return similar;
    }

    @Nullable public TranslationsDTO getTranslations()
    {
        return translations;
    }

    @Nullable public VideosDTO getVideos()
    {
        return videos;
    }
}
