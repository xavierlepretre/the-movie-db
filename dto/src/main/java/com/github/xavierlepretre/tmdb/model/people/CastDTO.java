package com.github.xavierlepretre.tmdb.model.people;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;

public class CastDTO extends PersonDTO
{
    @NonNull private final CastId castId;
    @NonNull private final String character;
    private final int order;

    public CastDTO(
            @JsonProperty(value = "cast_id", required = true) @NonNull CastId castId,
            @JsonProperty(value = "character", required = true) @NonNull String character,
            @JsonProperty(value = "credit_id", required = true) @NonNull CreditId creditId,
            @JsonProperty(value = "id", required = true) @NonNull PersonId id,
            @JsonProperty(value = "name", required = true) @NonNull String name,
            @JsonProperty(value = "order", required = true) int order,
            @JsonProperty(value = "profile_path", required = true) @NonNull ImagePath profilePath)
    {
        super(creditId,
                id,
                name,
                profilePath);
        this.castId = castId;
        this.character = character;
        this.order = order;
    }

    @NonNull public CastId getCastId()
    {
        return castId;
    }

    @NonNull public String getCharacter()
    {
        return character;
    }

    public int getOrder()
    {
        return order;
    }
}
