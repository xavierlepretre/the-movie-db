package com.github.xavierlepretre.tmdb.model.show;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class ReleasesDTO
{
    @NonNull private final List<ReleaseCountryDTO> countries;

    public ReleasesDTO(
            @JsonProperty(value = "countries", required = true) @NonNull
            List<ReleaseCountryDTO> countries)
    {
        this.countries = Collections.unmodifiableList(countries);
    }

    @NonNull public List<ReleaseCountryDTO> getCountries()
    {
        return countries;
    }
}
