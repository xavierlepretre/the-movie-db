package com.github.xavierlepretre.tmdb.model.i18n;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

public class TranslationDTO extends SpokenLanguageDTO
{
    @NonNull private final String englishName;

    public TranslationDTO(
            @JsonProperty(value = "iso_639_1", required = true) @NonNull
            Locale iso639Dash1,
            @JsonProperty(value = "name", required = true) @NonNull
            String name,
            @JsonProperty(value = "english_name", required = true) @NonNull
            String englishName)
    {
        super(iso639Dash1, name);
        this.englishName = englishName;
    }

    @NonNull public String getEnglishName()
    {
        return englishName;
    }
}
