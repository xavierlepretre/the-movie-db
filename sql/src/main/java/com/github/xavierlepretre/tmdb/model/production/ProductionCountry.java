package com.github.xavierlepretre.tmdb.model.production;

import android.support.annotation.Nullable;

import com.neovisionaries.i18n.CountryCode;

public class ProductionCountry
{
    @Nullable private final CountryCode iso3166Dash1;
    @Nullable private final String name;

    public ProductionCountry(
            @Nullable CountryCode iso3166Dash1,
            @Nullable String name)
    {
        this.iso3166Dash1 = iso3166Dash1;
        this.name = name;
    }

    @Nullable public CountryCode getIso3166Dash1()
    {
        return iso3166Dash1;
    }

    @Nullable public String getName()
    {
        return name;
    }
}
