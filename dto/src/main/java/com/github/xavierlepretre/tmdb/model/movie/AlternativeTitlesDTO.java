package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class AlternativeTitlesDTO
{
    @NonNull private final List<TitleDTO> titles;

    public AlternativeTitlesDTO(
            @JsonProperty(value = "titles", required = true) @NonNull
            List<TitleDTO> titles)
    {
        this.titles = Collections.unmodifiableList(titles);
    }

    @NonNull public List<TitleDTO> getTitles()
    {
        return titles;
    }
}
