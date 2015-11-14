package com.github.xavierlepretre.tmdb;

public class TmdbDtoConstants
{
    private TmdbDtoConstants()
    {
        throw new IllegalArgumentException("No instance");
    }

    public static class Movie
    {
        public static final String EXTRA_ALTERNATIVE_TITLES = "alternative_titles";
        public static final String EXTRA_CREDITS = "credits";
        public static final String EXTRA_IMAGES = "images";
        public static final String EXTRA_KEYWORDS = "keywords";
        public static final String EXTRA_LISTS = "lists";
        public static final String EXTRA_RELEASES = "releases";
        public static final String EXTRA_REVIEWS = "reviews";
        public static final String EXTRA_SIMILAR = "similar";
        public static final String EXTRA_TRANSLATIONS = "translations";
        public static final String EXTRA_VIDEOS = "videos";
    }
}
