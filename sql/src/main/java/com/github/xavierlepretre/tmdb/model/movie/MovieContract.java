package com.github.xavierlepretre.tmdb.model.movie;

import android.provider.BaseColumns;

public class MovieContract implements BaseColumns
{
    public static final String PATH = "movie";

    public static final String TABLE_NAME = "movie";

    // The id is a Long.

    // Adult is a Boolean coded as an Integer. 1 -> true, 0 -> false.
    public static final String COLUMN_ADULT = "adult";

    // BackdropPath is a String.
    public static final String COLUMN_BACKDROP_PATH = "backdropPath";

    // BelongsToCollectionId is a Long.
    public static final String COLUMN_BELONGS_TO_COLLECTION_ID = "belongsToCollectionId";

    // Budget is a Long.
    public static final String COLUMN_BUDGET = "budget";

    // Homepage is a String.
    public static final String COLUMN_HOMEPAGE = "homepage";

    // ImdbId is a String.
    public static final String COLUMN_IMDB_ID = "imdbId";

    // OriginalLanguage is a 2-character long String.
    public static final String COLUMN_ORIGINAL_LANGUAGE = "originalLanguage";

    // OriginalTitle is a String.
    public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";

    // Overview is a String.
    public static final String COLUMN_OVERVIEW = "overview";

    // Popularity is a Float.
    public static final String COLUMN_POPULARITY = "popularity";

    // PosterPath is a String.
    public static final String COLUMN_POSTER_PATH = "posterPath";

    // ReleaseDate is a String.
    public static final String COLUMN_RELEASE_DATE = "releaseDate";
    public static final String RELEASE_DATE_FORMAT = "yyyy-MM-dd";
    public static final String RELEASE_DATE_TIME_ZONE = "UTC";

    // Revenue is a Long.
    public static final String COLUMN_REVENUE = "revenue";

    // Runtime is an Integer.
    public static final String COLUMN_RUNTIME = "runtime";

    // Status is a String.
    public static final String COLUMN_STATUS = "status";

    // Tagline is a String.
    public static final String COLUMN_TAGLINE = "tagline";

    // Title is a String.
    public static final String COLUMN_TITLE = "title";

    // Video is a Boolean.
    public static final String COLUMN_VIDEO = "video";

    // VoteAverage is a Float.
    public static final String COLUMN_VOTE_AVERAGE = "voteAverage";

    // VoteCount is an Integer.
    public static final String COLUMN_VOTE_COUNT = "voteCount";
}
