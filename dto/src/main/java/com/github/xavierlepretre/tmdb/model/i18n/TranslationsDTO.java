package com.github.xavierlepretre.tmdb.model.i18n;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class TranslationsDTO
{
    @NonNull private final List<TranslationDTO> translations;

    public TranslationsDTO(
            @JsonProperty(value = "translations", required = true) @NonNull
            List<TranslationDTO> translations)
    {
        this.translations = Collections.unmodifiableList(translations);
    }

    @NonNull public List<TranslationDTO> getTranslations()
    {
        return translations;
    }
}
