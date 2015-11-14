package com.github.xavierlepretre.tmdb.model.image;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import java.util.List;

public class ImagesWithIdDTO extends ImagesDTO
{
    @NonNull private final MovieId id;

    public ImagesWithIdDTO(
            @JsonProperty(value = "id", required = true) @NonNull MovieId id,
            @JsonProperty(value = "backdrops", required = true) @NonNull List<ImageDTO> backdrops,
            @JsonProperty(value = "posters", required = true) @NonNull List<ImageDTO> posters)
    {
        super(backdrops, posters);
        this.id = id;
    }

    @NonNull public MovieId getId()
    {
        return id;
    }
}
