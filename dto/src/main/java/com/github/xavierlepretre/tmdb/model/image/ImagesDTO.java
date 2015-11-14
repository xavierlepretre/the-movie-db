package com.github.xavierlepretre.tmdb.model.image;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ImagesDTO
{
    @NonNull private final List<ImageDTO> backdrops;
    @NonNull private final List<ImageDTO> posters;

    public ImagesDTO(
            @JsonProperty(value = "backdrops", required = true) @NonNull List<ImageDTO> backdrops,
            @JsonProperty(value = "posters", required = true) @NonNull List<ImageDTO> posters)
    {
        this.backdrops = backdrops;
        this.posters = posters;
    }

    @NonNull public List<ImageDTO> getBackdrops()
    {
        return backdrops;
    }

    @NonNull public List<ImageDTO> getPosters()
    {
        return posters;
    }
}
