package com.github.xavierlepretre.tmdb.model.production;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

public class ProductionCountryDTO
{
    @NonNull private final CountryCode iso3166Dash1;
    @NonNull private final String name;

    public ProductionCountryDTO(
            @JsonProperty(value = "iso_3166_1", required = true) @NonNull CountryCode iso3166Dash1,
            @JsonProperty(value = "name", required = true) @NonNull String name)
    {
        this.iso3166Dash1 = iso3166Dash1;
        this.name = name;
    }

    @NonNull public CountryCode getIso3166Dash1()
    {
        return iso3166Dash1;
    }

    @NonNull public String getName()
    {
        return name;
    }
}
