package com.github.xavierlepretre.tmdb.model;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;

public class TmdbSyncConstants
{
    private static final String BUNDLE_KEY_LANGUAGE = "TMDB.Language";

    public static void putLanguage(@NonNull Bundle extras, @Nullable Locale language)
    {
        extras.putSerializable(BUNDLE_KEY_LANGUAGE, language);
    }

    @Nullable public static Locale getLanguage(@NonNull Bundle extras)
    {
        return (Locale) extras.getSerializable(BUNDLE_KEY_LANGUAGE);
    }
}
