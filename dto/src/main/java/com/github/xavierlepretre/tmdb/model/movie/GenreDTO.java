package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class GenreDTO
{
    @NonNull private final GenreId id;
    @NonNull private final String name;

    public GenreDTO(
            @JsonProperty(value = "id", required = true) @NonNull GenreId id,
            @JsonProperty(value = "name", required = true) @NonNull String name)
    {
        this.id = id;
        this.name = name;
    }

    @NonNull public GenreId getId()
    {
        return id;
    }

    @NonNull public String getName()
    {
        return name;
    }

    @NonNull public static List<GenreId> getIds(@NonNull List<GenreDTO> genres)
    {
        List<GenreId> ids = new ArrayList<>();
        for (GenreDTO genre : genres)
        {
            ids.add(genre.getId());
        }
        return ids;
    }
}
