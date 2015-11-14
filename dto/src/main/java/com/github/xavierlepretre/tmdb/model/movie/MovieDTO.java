package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.i18n.SpokenLanguageDTO;
import com.github.xavierlepretre.tmdb.model.production.ProductionCompanyDTO;
import com.github.xavierlepretre.tmdb.model.production.ProductionCountryDTO;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieDTO extends MovieShortDTO
{
    @NonNull private final CollectionDTO belongsToCollection;
    private final long budget;
    @NonNull private final List<GenreDTO> genres;
    @NonNull private final String homepage;
    @NonNull private final ImdbId imdbId;
    @NonNull private final List<ProductionCompanyDTO> productionCompanies;
    @NonNull private final List<ProductionCountryDTO> productionCountries;
    private final long revenue;
    private final int runtime;
    @NonNull private final List<SpokenLanguageDTO> spokenLanguages;
    @NonNull private final String status;
    @NonNull private final String tagline;

    public MovieDTO(
            @JsonProperty(value = "adult", required = true)
            boolean adult,
            @JsonProperty(value = "backdrop_path", required = true) @NonNull
            String backdropPath,
            @JsonProperty(value = "belongs_to_collection", required = true) @NonNull
            CollectionDTO belongsToCollection,
            @JsonProperty(value = "budget", required = true)
            long budget,
            @JsonProperty(value = "genres", required = true) @NonNull
            List<GenreDTO> genres,
            @JsonProperty(value = "homepage", required = true) @NonNull
            String homepage,
            @JsonProperty(value = "id", required = true) @NonNull
            MovieId id,
            @JsonProperty(value = "imdb_id", required = true) @NonNull
            ImdbId imdbId,
            @JsonProperty(value = "original_language", required = true) @NonNull
            Locale originalLanguage,
            @JsonProperty(value = "original_title", required = true) @NonNull
            String originalTitle,
            @JsonProperty(value = "overview", required = true) @NonNull
            String overview,
            @JsonProperty(value = "popularity", required = true)
            float popularity,
            @JsonProperty(value = "poster_path", required = true) @NonNull
            String posterPath,
            @JsonProperty(value = "production_companies", required = true) @NonNull
            List<ProductionCompanyDTO> productionCompanies,
            @JsonProperty(value = "production_countries", required = true) @NonNull
            List<ProductionCountryDTO> productionCountries,
            @JsonProperty(value = "release_date", required = true) @NonNull
            Date releaseDate,
            @JsonProperty(value = "revenue", required = true)
            long revenue,
            @JsonProperty(value = "runtime", required = true)
            int runtime,
            @JsonProperty(value = "spoken_languages", required = true) @NonNull
            List<SpokenLanguageDTO> spokenLanguages,
            @JsonProperty(value = "status", required = true) @NonNull
            String status,
            @JsonProperty(value = "tagline", required = true) @NonNull
            String tagline,
            @JsonProperty(value = "title", required = true) @NonNull
            String title,
            @JsonProperty(value = "video", required = true)
            boolean video,
            @JsonProperty(value = "vote_average", required = true)
            float voteAverage,
            @JsonProperty(value = "vote_count", required = true) @NonNull
            Integer voteCount)
    {
        super(adult,
                backdropPath,
                GenreDTO.getIds(genres),
                id,
                originalLanguage,
                originalTitle,
                overview,
                popularity, posterPath,
                releaseDate,
                title,
                video,
                voteAverage,
                voteCount);
        this.belongsToCollection = belongsToCollection;
        this.budget = budget;
        this.genres = Collections.unmodifiableList(genres);
        this.homepage = homepage;
        this.imdbId = imdbId;
        this.productionCompanies = Collections.unmodifiableList(productionCompanies);
        this.productionCountries = Collections.unmodifiableList(productionCountries);
        this.revenue = revenue;
        this.runtime = runtime;
        this.spokenLanguages = Collections.unmodifiableList(spokenLanguages);
        this.status = status;
        this.tagline = tagline;
    }

    @NonNull public CollectionDTO getBelongsToCollection()
    {
        return belongsToCollection;
    }

    public long getBudget()
    {
        return budget;
    }

    @NonNull public List<GenreDTO> getGenres()
    {
        return genres;
    }

    @NonNull public String getHomepage()
    {
        return homepage;
    }

    @NonNull public ImdbId getImdbId()
    {
        return imdbId;
    }

    @NonNull public List<ProductionCompanyDTO> getProductionCompanies()
    {
        return productionCompanies;
    }

    @NonNull public List<ProductionCountryDTO> getProductionCountries()
    {
        return productionCountries;
    }

    public long getRevenue()
    {
        return revenue;
    }

    public int getRuntime()
    {
        return runtime;
    }

    @NonNull public List<SpokenLanguageDTO> getSpokenLanguages()
    {
        return spokenLanguages;
    }

    @NonNull public String getStatus()
    {
        return status;
    }

    @NonNull public String getTagline()
    {
        return tagline;
    }
}
