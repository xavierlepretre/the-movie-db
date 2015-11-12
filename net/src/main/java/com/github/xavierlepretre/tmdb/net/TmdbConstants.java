package com.github.xavierlepretre.tmdb.net;

public class TmdbConstants
{
    public static final String CONTENT_URL = "https://api.themoviedb.org/3/";

    public static final String QUERY_API_KEY = "api_key";

    public static class Configuration
    {
        public static final String PATH_CONFIGURATION = "configuration";

        public static String getConfigurationPath()
        {
            return CONTENT_URL + PATH_CONFIGURATION;
        }
    }
}
