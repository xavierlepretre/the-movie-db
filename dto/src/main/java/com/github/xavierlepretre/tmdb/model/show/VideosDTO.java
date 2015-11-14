package com.github.xavierlepretre.tmdb.model.show;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class VideosDTO
{
    @NonNull private List<VideoDTO> results;

    public VideosDTO(
            @JsonProperty(value = "results", required = true) @NonNull List<VideoDTO> results)
    {
        this.results = Collections.unmodifiableList(results);
    }

    @NonNull public List<VideoDTO> getResults()
    {
        return results;
    }
}
