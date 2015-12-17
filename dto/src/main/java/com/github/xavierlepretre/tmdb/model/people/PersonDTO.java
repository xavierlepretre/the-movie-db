package com.github.xavierlepretre.tmdb.model.people;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.image.HasProfilePathDTO;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;

public class PersonDTO implements HasProfilePathDTO
{
    @NonNull private final CreditId creditId;
    @NonNull private final PersonId id;
    @NonNull private final String name;
    @NonNull private final ImagePath profilePath;

    public PersonDTO(
            @JsonProperty(value = "credit_id", required = true) @NonNull CreditId creditId,
            @JsonProperty(value = "id", required = true) @NonNull PersonId id,
            @JsonProperty(value = "name", required = true) @NonNull String name,
            @JsonProperty(value = "profile_path", required = true) @NonNull ImagePath profilePath)
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

    @NonNull @Override public ImagePath getProfilePath()
    {
        return profilePath;
    }
}
