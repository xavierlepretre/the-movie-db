package com.github.xavierlepretre.tmdb.model;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.TmdbDtoConstants;

public class AppendableRequest
{
    @NonNull private final String request;

    AppendableRequest(@NonNull String request)
    {
        this.request = request;
    }

    @Override public String toString()
    {
        return request;
    }

    @Override public final int hashCode()
    {
        return request.hashCode();
    }

    @Override public final boolean equals(Object obj)
    {
        return obj instanceof AppendableRequest
                && obj.toString().equals(request);
    }

    public static class Movie
    {
        public static final AppendableRequest ALTERNATIVE_TITLES = new AppendableRequest(TmdbDtoConstants.Movie.EXTRA_ALTERNATIVE_TITLES);
        public static final AppendableRequest CREDITS = new AppendableRequest(TmdbDtoConstants.Movie.EXTRA_CREDITS);
        public static final AppendableRequest IMAGES = new AppendableRequest(TmdbDtoConstants.Movie.EXTRA_IMAGES);
        public static final AppendableRequest KEYWORDS = new AppendableRequest(TmdbDtoConstants.Movie.EXTRA_KEYWORDS);
        public static final AppendableRequest LISTS = new AppendableRequest(TmdbDtoConstants.Movie.EXTRA_LISTS);
        public static final AppendableRequest RELEASES = new AppendableRequest(TmdbDtoConstants.Movie.EXTRA_RELEASES);
        public static final AppendableRequest REVIEWS = new AppendableRequest(TmdbDtoConstants.Movie.EXTRA_REVIEWS);
        public static final AppendableRequest SIMILAR = new AppendableRequest(TmdbDtoConstants.Movie.EXTRA_SIMILAR);
        public static final AppendableRequest TRANSLATIONS = new AppendableRequest(TmdbDtoConstants.Movie.EXTRA_TRANSLATIONS);
        public static final AppendableRequest VIDEOS = new AppendableRequest(TmdbDtoConstants.Movie.EXTRA_VIDEOS);
    }
}
