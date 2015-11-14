package com.github.xavierlepretre.tmdb.model.show;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.movie.MovieId;

import java.util.List;

public class VideosWithIdDTO extends VideosDTO
{
    @NonNull private final MovieId id;

    public VideosWithIdDTO(
            @JsonProperty(value = "id", required = true)
            MovieId id,
            @JsonProperty(value = "results", required = true) @NonNull
            List<VideoDTO> results)
    {
        super(results);
        this.id = id;
    }

    @NonNull public MovieId getId()
    {
        return id;
    }
}
