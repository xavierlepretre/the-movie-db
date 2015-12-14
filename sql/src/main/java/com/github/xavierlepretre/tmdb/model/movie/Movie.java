package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.Nullable;

import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.neovisionaries.i18n.LanguageCode;

import java.util.Date;

public class Movie
{
    @Nullable private final Boolean adult;
    @Nullable private final ImagePath backdropPath;
    @Nullable private final CollectionId belongsToCollectionId;
    @Nullable private final Long budget;
    @Nullable private final String homepage;
    @Nullable private final MovieId id;
    @Nullable private final ImdbId imdbId;
    @Nullable private final LanguageCode originalLanguage;
    @Nullable private final String originalTitle;
    @Nullable private final String overview;
    @Nullable private final Float popularity;
    @Nullable private final ImagePath posterPath;
    @Nullable private final Date releaseDate;
    @Nullable private final Long revenue;
    @Nullable private final Integer runtime;
    @Nullable private final String status;
    @Nullable private final String tagline;
    @Nullable private final String title;
    @Nullable private final Boolean video;
    @Nullable private final Float voteAverage;
    @Nullable private final Integer voteCount;

    public Movie(
            @Nullable Boolean adult,
            @Nullable ImagePath backdropPath,
            @Nullable CollectionId belongsToCollectionId,
            @Nullable Long budget,
            @Nullable String homepage,
            @Nullable MovieId id,
            @Nullable ImdbId imdbId,
            @Nullable LanguageCode originalLanguage,
            @Nullable String originalTitle,
            @Nullable String overview,
            @Nullable Float popularity,
            @Nullable ImagePath posterPath,
            @Nullable Date releaseDate,
            @Nullable Long revenue,
            @Nullable Integer runtime,
            @Nullable String status,
            @Nullable String tagline,
            @Nullable String title,
            @Nullable Boolean video,
            @Nullable Float voteAverage,
            @Nullable Integer voteCount)
    {
        this.adult = adult;
        this.backdropPath = backdropPath;
        this.belongsToCollectionId = belongsToCollectionId;
        this.budget = budget;
        this.homepage = homepage;
        this.id = id;
        this.imdbId = imdbId;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.revenue = revenue;
        this.runtime = runtime;
        this.status = status;
        this.tagline = tagline;
        this.title = title;
        this.video = video;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
    }

    @Nullable public Boolean getAdult()
    {
        return adult;
    }

    @Nullable public ImagePath getBackdropPath()
    {
        return backdropPath;
    }

    @Nullable public CollectionId getBelongsToCollectionId()
    {
        return belongsToCollectionId;
    }

    @Nullable public Long getBudget()
    {
        return budget;
    }

    @Nullable public String getHomepage()
    {
        return homepage;
    }

    @Nullable public MovieId getId()
    {
        return id;
    }

    @Nullable public ImdbId getImdbId()
    {
        return imdbId;
    }

    @Nullable public LanguageCode getOriginalLanguage()
    {
        return originalLanguage;
    }

    @Nullable public String getOriginalTitle()
    {
        return originalTitle;
    }

    @Nullable public String getOverview()
    {
        return overview;
    }

    @Nullable public Float getPopularity()
    {
        return popularity;
    }

    @Nullable public ImagePath getPosterPath()
    {
        return posterPath;
    }

    @Nullable public Date getReleaseDate()
    {
        return releaseDate;
    }

    @Nullable public Long getRevenue()
    {
        return revenue;
    }

    @Nullable public Integer getRuntime()
    {
        return runtime;
    }

    @Nullable public String getStatus()
    {
        return status;
    }

    @Nullable public String getTagline()
    {
        return tagline;
    }

    @Nullable public String getTitle()
    {
        return title;
    }

    @Nullable public Boolean getVideo()
    {
        return video;
    }

    @Nullable public Float getVoteAverage()
    {
        return voteAverage;
    }

    @Nullable public Integer getVoteCount()
    {
        return voteCount;
    }
}
