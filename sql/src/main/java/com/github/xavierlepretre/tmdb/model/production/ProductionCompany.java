package com.github.xavierlepretre.tmdb.model.production;

import android.support.annotation.Nullable;

public class ProductionCompany
{
    @Nullable private final ProductionCompanyId id;
    @Nullable private final String name;

    public ProductionCompany(
            @Nullable ProductionCompanyId id,
            @Nullable String name)
    {
        this.id = id;
        this.name = name;
    }

    @Nullable public ProductionCompanyId getId()
    {
        return id;
    }

    @Nullable public String getName()
    {
        return name;
    }
}
