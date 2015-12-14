package com.github.xavierlepretre.tmdb.model.i18n;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.LanguageCode;

public class SpokenLanguageDTO
{
    @NonNull private final LanguageCode iso639Dash1;
    @NonNull private final String name;

    public SpokenLanguageDTO(
            @JsonProperty(value = "iso_639_1", required = true) @NonNull LanguageCode iso639Dash1,
            @JsonProperty(value = "name", required = true) @NonNull String name)
    {
        this.iso639Dash1 = iso639Dash1;
        this.name = name;
    }

    @NonNull public LanguageCode getIso639Dash1()
    {
        return iso639Dash1;
    }

    @NonNull public String getName()
    {
        return name;
    }
}
