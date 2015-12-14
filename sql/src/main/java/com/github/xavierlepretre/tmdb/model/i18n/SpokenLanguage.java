package com.github.xavierlepretre.tmdb.model.i18n;

import android.support.annotation.Nullable;

import com.neovisionaries.i18n.LanguageCode;

public class SpokenLanguage
{
    @Nullable private final LanguageCode iso639Dash1;
    @Nullable private final String name;

    public SpokenLanguage(
            @Nullable LanguageCode iso639Dash1,
            @Nullable String name)
    {
        this.iso639Dash1 = iso639Dash1;
        this.name = name;
    }

    @Nullable public LanguageCode getIso639Dash1()
    {
        return iso639Dash1;
    }

    @Nullable public String getName()
    {
        return name;
    }
}
