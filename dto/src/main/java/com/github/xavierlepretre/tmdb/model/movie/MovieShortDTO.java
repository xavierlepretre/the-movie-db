package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieShortDTO
{
    @NonNull private final Boolean adult;
    @NonNull private final String backdropPath;
    @NonNull private final List<GenreId> genreIds;
    @NonNull private final MovieId id;
    @NonNull private final Locale originalLanguage;
    @NonNull private final String originalTitle;
    @NonNull private final String overview;
    @NonNull private final String posterPath;
    @NonNull private final Float popularity;
    @NonNull private final Date releaseDate;
    @NonNull private final String title;
    @NonNull private final Boolean video;
    @NonNull private final Float voteAverage;
    @NonNull private final Integer voteCount;

    public MovieShortDTO(
            @JsonProperty(value = "adult", required = true)
            boolean adult,
            @JsonProperty(value = "backdrop_path", required = true) @NonNull
            String backdropPath,
            @JsonProperty(value = "genre_ids", required = true) @NonNull
            List<GenreId> genreIds,
            @JsonProperty(value = "id", required = true) @NonNull
            MovieId id,
            @JsonProperty(value = "original_language", required = true) @NonNull
            Locale originalLanguage,
            @JsonProperty(value = "original_title", required = true) @NonNull
            String originalTitle,
            @JsonProperty(value = "overview", required = true) @NonNull
            String overview,
            @JsonProperty(value = "poster_path", required = true) @NonNull
            String posterPath,
            @JsonProperty(value = "popularity", required = true)
            float popularity,
            @JsonProperty(value = "release_date", required = true) @NonNull
            Date releaseDate,
            @JsonProperty(value = "title", required = true) @NonNull
            String title,
            @JsonProperty(value = "video", required = true)
            boolean video,
            @JsonProperty(value = "vote_average", required = true)
            float voteAverage,
            @JsonProperty(value = "vote_count", required = true) @NonNull
            Integer voteCount)
    {
        this.backdropPath = backdropPath;
        this.genreIds = Collections.unmodifiableList(genreIds);
        this.id = id;
        this.adult = adult;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.posterPath = posterPath;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.title = title;
        this.video = video;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
    }

    public boolean getAdult()
    {
        return adult;
    }

    @NonNull public String getBackdropPath()
    {
        return backdropPath;
    }

    @NonNull public List<GenreId> getGenreIds()
    {
        return genreIds;
    }

    @NonNull public MovieId getId()
    {
        return id;
    }

    @NonNull public Locale getOriginalLanguage()
    {
        return originalLanguage;
    }

    @NonNull public String getOriginalTitle()
    {
        return originalTitle;
    }

    @NonNull public String getOverview()
    {
        return overview;
    }

    @NonNull public Date getReleaseDate()
    {
        return releaseDate;
    }

    @NonNull public String getPosterPath()
    {
        return posterPath;
    }

    public float getPopularity()
    {
        return popularity;
    }

    @NonNull public String getTitle()
    {
        return title;
    }

    public boolean getVideo()
    {
        return video;
    }

    public float getVoteAverage()
    {
        return voteAverage;
    }

    public int getVoteCount()
    {
        return voteCount;
    }
}
