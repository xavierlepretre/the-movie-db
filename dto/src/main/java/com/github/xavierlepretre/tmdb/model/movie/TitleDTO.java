package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

public class TitleDTO
{
    @NonNull private final CountryCode iso3166Dash1;
    @NonNull private final String title;

    public TitleDTO(
            @JsonProperty(value = "iso_3166_1", required = true) @NonNull CountryCode iso3166Dash1,
            @JsonProperty(value = "title", required = true) @NonNull String title)
    {
        this.iso3166Dash1 = iso3166Dash1;
        this.title = title;
    }

    @NonNull public CountryCode getIso3166Dash1()
    {
        return iso3166Dash1;
    }

    @NonNull public String getTitle()
    {
        return title;
    }
}
