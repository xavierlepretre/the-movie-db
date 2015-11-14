package com.github.xavierlepretre.tmdb.model.people;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonDTO
{
    @NonNull private final CreditId creditId;
    @NonNull private final PersonId id;
    @NonNull private final String name;
    @NonNull private final String profilePath;

    public PersonDTO(
            @JsonProperty(value = "credit_id", required = true) @NonNull CreditId creditId,
            @JsonProperty(value = "id", required = true) @NonNull PersonId id,
            @JsonProperty(value = "name", required = true) @NonNull String name,
            @JsonProperty(value = "profile_path", required = true) @NonNull String profilePath)
    {
        this.creditId = creditId;
        this.id = id;
        this.name = name;
        this.profilePath = profilePath;
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

    @NonNull public String getProfilePath()
    {
        return profilePath;
    }
}
