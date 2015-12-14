package com.github.xavierlepretre.tmdb.sync;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.neovisionaries.i18n.LanguageCode;

public class TmdbSyncConstants
{
    private static final String BUNDLE_KEY_LANGUAGE = "TMDB.Language";

    public static void putLanguage(@NonNull Bundle extras, @Nullable LanguageCode language)
    {
        extras.putSerializable(BUNDLE_KEY_LANGUAGE, language);
    }

    @Nullable public static LanguageCode getLanguage(@NonNull Bundle extras)
    {
        return (LanguageCode) extras.getSerializable(BUNDLE_KEY_LANGUAGE);
    }
}
