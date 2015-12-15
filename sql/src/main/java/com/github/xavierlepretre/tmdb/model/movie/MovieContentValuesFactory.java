package com.github.xavierlepretre.tmdb.model.movie;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.TimeZone;
import java.util.Vector;

public class MovieContentValuesFactory
{
    @NonNull private final SimpleDateFormat formatter;

    @SuppressLint("SimpleDateFormat")
    public MovieContentValuesFactory()
    {
        formatter = new SimpleDateFormat(MovieContract.RELEASE_DATE_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone(MovieContract.RELEASE_DATE_TIME_ZONE));
    }

    @NonNull public Vector<ContentValues> createFrom(@NonNull Collection<MovieDTO> movieDTOs)
    {
        Vector<ContentValues> values = new Vector<>(movieDTOs.size());
        for (MovieDTO movieDTO : movieDTOs)
        {
            values.add(createFrom(movieDTO));
        }
        return values;
    }

    @NonNull public Vector<ContentValues> createFrom(
            @NonNull Collection<MovieShortDTO> movieShortDTOs,
            @SuppressWarnings("UnusedParameters") @Nullable MovieShortDTO typeQualifier)
    {
        Vector<ContentValues> values = new Vector<>(movieShortDTOs.size());
        for (MovieShortDTO movieShortDTO : movieShortDTOs)
        {
            values.add(createFrom(movieShortDTO));
        }
        return values;
    }

    @NonNull public Vector<ContentValues> createFrom(
            @NonNull Collection<MovieId> movieIds,
            @SuppressWarnings("UnusedParameters") @Nullable MovieId typeQualifier)
    {
        Vector<ContentValues> values = new Vector<>(movieIds.size());
        for (MovieId movieId : movieIds)
        {
            values.add(createFrom(movieId));
        }
        return values;
    }

    @NonNull public ContentValues createFrom(@NonNull MovieDTO movieDTO)
    {
        ContentValues contentValues = new ContentValues();
        populate(contentValues, movieDTO);
        return contentValues;
    }

    @NonNull public ContentValues createFrom(@NonNull MovieShortDTO movieDTO)
    {
        ContentValues contentValues = new ContentValues();
        populate(contentValues, movieDTO);
        return contentValues;
    }

    @NonNull public ContentValues createFrom(@NonNull MovieId movieId)
    {
        ContentValues contentValues = new ContentValues();
        populate(contentValues, movieId);
        return contentValues;
    }

    public void populate(@NonNull ContentValues contentValues, @NonNull MovieDTO movieDTO)
    {
        populate(contentValues, (MovieShortDTO) movieDTO);
        contentValues.put(MovieContract.COLUMN_BELONGS_TO_COLLECTION_ID, movieDTO.getBelongsToCollection().getId().getId());
        contentValues.put(MovieContract.COLUMN_BUDGET, movieDTO.getBudget());
        contentValues.put(MovieContract.COLUMN_HOMEPAGE, movieDTO.getHomepage());
        contentValues.put(MovieContract.COLUMN_IMDB_ID, movieDTO.getImdbId().getId());
        contentValues.put(MovieContract.COLUMN_REVENUE, movieDTO.getRevenue());
        contentValues.put(MovieContract.COLUMN_RUNTIME, movieDTO.getRuntime());
        contentValues.put(MovieContract.COLUMN_STATUS, movieDTO.getStatus());
        contentValues.put(MovieContract.COLUMN_TAGLINE, movieDTO.getTagline());
    }

    public void populate(@NonNull ContentValues contentValues, @NonNull MovieShortDTO movieShortDTO)
    {
        contentValues.put(MovieContract.COLUMN_ADULT, movieShortDTO.getAdult());
        contentValues.put(MovieContract.COLUMN_BACKDROP_PATH, movieShortDTO.getBackdropPath().getPath());
        populate(contentValues, movieShortDTO.getId());
        contentValues.put(MovieContract.COLUMN_ORIGINAL_LANGUAGE, movieShortDTO.getOriginalLanguage().name());
        contentValues.put(MovieContract.COLUMN_ORIGINAL_TITLE, movieShortDTO.getOriginalTitle());
        contentValues.put(MovieContract.COLUMN_OVERVIEW, movieShortDTO.getOverview());
        contentValues.put(MovieContract.COLUMN_POPULARITY, movieShortDTO.getPopularity());
        contentValues.put(MovieContract.COLUMN_POSTER_PATH, movieShortDTO.getPosterPath().getPath());
        contentValues.put(MovieContract.COLUMN_RELEASE_DATE, formatter.format(movieShortDTO.getReleaseDate()));
        contentValues.put(MovieContract.COLUMN_TITLE, movieShortDTO.getTitle());
        contentValues.put(MovieContract.COLUMN_VIDEO, movieShortDTO.getVideo());
        contentValues.put(MovieContract.COLUMN_VOTE_AVERAGE, movieShortDTO.getVoteAverage());
        contentValues.put(MovieContract.COLUMN_VOTE_COUNT, movieShortDTO.getVoteCount());
    }

    public void populate(@NonNull ContentValues contentValues, @NonNull MovieId movieId)
    {
        contentValues.put(MovieContract._ID, movieId.getId());
    }
}
