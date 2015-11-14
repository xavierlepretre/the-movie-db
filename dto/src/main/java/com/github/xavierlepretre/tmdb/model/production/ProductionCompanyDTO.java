package com.github.xavierlepretre.tmdb.model.production;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductionCompanyDTO
{
    @NonNull private final ProductionCompanyId id;
    @NonNull private final String name;

    public ProductionCompanyDTO(
            @JsonProperty(value = "id", required = true) @NonNull ProductionCompanyId id,
            @JsonProperty(value = "name", required = true) @NonNull String name)
    {
        this.id = id;
        this.name = name;
    }

    @NonNull public ProductionCompanyId getId()
    {
        return id;
    }

    @NonNull public String getName()
    {
        return name;
    }
}
