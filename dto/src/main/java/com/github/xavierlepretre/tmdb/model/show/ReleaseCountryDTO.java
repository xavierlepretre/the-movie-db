package com.github.xavierlepretre.tmdb.model.show;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import java.util.Date;

public class ReleaseCountryDTO
{
    @Nullable private final String certification;
    @NonNull private final CountryCode iso3166Dash1;
    private final boolean primary;
    @NonNull private final Date releaseDate;

    public ReleaseCountryDTO(
            @JsonProperty(value = "certification", required = true) String certification,
            @JsonProperty(value = "iso_3166_1", required = true) @NonNull CountryCode iso3166Dash1,
            @JsonProperty(value = "primary", required = true) boolean primary,
            @JsonProperty(value = "release_date", required = true) @NonNull Date releaseDate)
    {
        this.certification = certification;
        this.iso3166Dash1 = iso3166Dash1;
        this.primary = primary;
        this.releaseDate = releaseDate;
    }

    @Nullable public String getCertification()
    {
        return certification;
    }

    @NonNull public CountryCode getIso3166Dash1()
    {
        return iso3166Dash1;
    }

    public boolean isPrimary()
    {
        return primary;
    }

    @NonNull public Date getReleaseDate()
    {
        return releaseDate;
    }
}
