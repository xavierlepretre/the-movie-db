package com.github.xavierlepretre.tmdb.model.conf;

import android.provider.BaseColumns;

public class ConfigurationContract implements BaseColumns
{
    public static final String PATH = "configuration";

    public static final String TABLE_NAME = "configuration";
    public static final int UNIQUE_ROW_ID = 1;

    public static class ImagesConfSegment
    {
        // BaseUrl is a string.
        public static final String COLUMN_BASE_URL = "imagesBaseUrl";
        // SecureBaseUrl is a string.
        public static final String COLUMN_SECURE_BASE_URL = "imagesSecureBaseUrl";
        // BackdropSizes is a string that is a concatenation of strings.
        public static final String COLUMN_BACKDROP_SIZES = "imagesBackdropSizes";
        // LogoSizes is a string that is a concatenation of strings.
        public static final String COLUMN_LOGO_SIZES = "imagesLogoSizes";
        // PosterSizes is a string that is a concatenation of strings.
        public static final String COLUMN_POSTER_SIZES = "imagesPosterSizes";
        // ProfileSizes is a string that is a concatenation of strings.
        public static final String COLUMN_PROFILE_SIZES = "imagesProfileSizes";
        // StillSizes is a string that is a concatenation of strings.
        public static final String COLUMN_STILL_SIZES = "imagesStillSizes";
    }

    // ChangeKeys is a string that is a concatenation of strings.
    public static final String COLUMN_CHANGE_KEYS = "changeKeys";
}
