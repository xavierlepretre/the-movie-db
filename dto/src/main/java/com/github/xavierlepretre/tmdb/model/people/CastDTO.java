package com.github.xavierlepretre.tmdb.model.people;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CastDTO
{
    @NonNull private final CastId castId;
    @NonNull private final String character;
    @NonNull private final CreditId creditId;
    @NonNull private final PersonId id;
    @NonNull private final String name;
    private final int order;
    @NonNull private final String profilePath;

    public CastDTO(
            @JsonProperty(value = "cast_id", required = true) @NonNull CastId castId,
            @JsonProperty(value = "character", required = true) @NonNull String character,
            @JsonProperty(value = "credit_id", required = true) @NonNull CreditId creditId,
            @JsonProperty(value = "id", required = true) @NonNull PersonId id,
            @JsonProperty(value = "name", required = true) @NonNull String name,
            @JsonProperty(value = "order", required = true) int order,
            @JsonProperty(value = "profile_path", required = true) @NonNull String profilePath)
    {
        this.castId = castId;
        this.character = character;
        this.creditId = creditId;
        this.id = id;
        this.name = name;
        this.order = order;
        this.profilePath = profilePath;
    }

    @NonNull public CastId getCastId()
    {
        return castId;
    }

    @NonNull public String getCharacter()
    {
        return character;
    }

    @NonNull public CreditId getCreditId()
    {
        return creditId;
    }

    @NonNull public PersonId getId()
    {
        return id;
    }

    @NonNull public String getName()
    {
        return name;
    }

    public int getOrder()
    {
        return order;
    }

    @NonNull public String getProfilePath()
    {
        return profilePath;
    }
}
