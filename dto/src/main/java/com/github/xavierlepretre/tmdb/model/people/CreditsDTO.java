package com.github.xavierlepretre.tmdb.model.people;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class CreditsDTO
{
    @NonNull private final List<CastDTO> cast;
    @NonNull private final List<CrewDTO> crew;

    public CreditsDTO(
            @JsonProperty(value = "cast", required = true) @NonNull List<CastDTO> cast,
            @JsonProperty(value = "crew", required = true) @NonNull List<CrewDTO> crew)
    {
        this.cast = Collections.unmodifiableList(cast);
        this.crew = Collections.unmodifiableList(crew);
    }

    @NonNull public List<CastDTO> getCast()
    {
        return cast;
    }

    @NonNull public List<CrewDTO> getCrew()
    {
        return crew;
    }
}
