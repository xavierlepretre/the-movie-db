package com.github.xavierlepretre.tmdb.model.movie;

import android.provider.BaseColumns;

public class CollectionContract implements BaseColumns
{
    public static final String PATH = "collection";

    public static final String TABLE_NAME = "collection";

    // The backdrop path is a string.
    public static final String COLUMN_BACKDROP_PATH = "backdropPath";

    // The name is a string.
    public static final String COLUMN_NAME = "name";

    // The poster path is a string.
    public static final String COLUMN_POSTER_PATH = "posterPath";
}
