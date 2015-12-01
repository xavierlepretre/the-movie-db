package com.github.xavierlepretre.tmdb.net;

import android.support.annotation.NonNull;

public class TmdbConstants
{
    public static final String CONTENT_URL = "https://api.themoviedb.org/3/";

    public static final String RESPONSE_HEADER_REQUEST_LIMIT = "X-RateLimit-Limit";
    public static final String RESPONSE_HEADER_REQUEST_REMAINING = "X-RateLimit-Remaining";
    public static final String RESPONSE_HEADER_REQUEST_REMAINING_RESET = "X-RateLimit-Reset";
    public static final int HTTP_CODE_REQUEST_LIMIT_EXCEEDED = 429;

    public static final String QUERY_API_KEY = "api_key";
    /**
     * To pass a ISO 639-1 code of a language.
     */
    public static final String QUERY_LANGUAGE = "language";
    public static final String QUERY_APPEND_TO_RESPONSE = "append_to_response";

    public static class Configuration
    {
        public static final String PATH_CONFIGURATION = "configuration";

        @NonNull public static String getConfigurationPath()
        {
            return CONTENT_URL + PATH_CONFIGURATION;
        }
    }

    public static class Discover
    {
        public static final String PATH_DISCOVER = "discover";
        public static final String PATH_DISCOVER_MOVIE = PATH_DISCOVER + "/" + Movie.PATH_MOVIE;

        @NonNull public static String getDiscoverMoviePath()
        {
            return CONTENT_URL + PATH_DISCOVER_MOVIE;
        }
    }

    public static class Movie
    {
        public static final String PATH_MOVIE = "movie";

        @NonNull public static String getMoviePath()
        {
            return CONTENT_URL + PATH_MOVIE;
        }
    }

    public static class Genre
    {
        public static final String PATH_GENRE = "genre";
    }
}
