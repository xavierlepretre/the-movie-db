package com.github.xavierlepretre.tmdb.model;

import android.content.ContentResolver;
import android.net.Uri;

import com.github.xavierlepretre.tmdb.model.movie.GenreContract;

public class TmdbContract
{
    public static final String CONTENT_AUTHORITY = "com.github.xavierlepretre.tmdb";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class GenreEntity extends GenreContract
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
    }
}
