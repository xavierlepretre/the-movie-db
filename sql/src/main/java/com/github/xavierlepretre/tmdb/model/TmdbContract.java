package com.github.xavierlepretre.tmdb.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.movie.GenreContract;
import com.github.xavierlepretre.tmdb.model.movie.GenreId;
import com.github.xavierlepretre.tmdb.model.movie.GenreProviderDelegate;

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

        @NonNull public static Uri buildUri(@NonNull GenreId genreId)
        {
            return GenreProviderDelegate.buildGenreLocation(CONTENT_URI, genreId.getId());
        }
    }
}
