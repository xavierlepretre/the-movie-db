package com.github.xavierlepretre.tmdb.model.production;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class ProductionCompanyId
{
    @NonNull private final Integer id;

    @JsonCreator
    public ProductionCompanyId(int id)
    {
        this.id = id;
    }

    @JsonValue public int getId()
    {
        return id;
    }

    @Override public final int hashCode()
    {
        return id.hashCode();
    }

    @Override public final boolean equals(Object obj)
    {
        return obj instanceof ProductionCompanyId
                && ((ProductionCompanyId) obj).getId() == id;
    }
}
