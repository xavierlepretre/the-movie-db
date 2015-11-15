package com.github.xavierlepretre.tmdb.model.tag;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class KeywordsDTO
{
    @NonNull private final List<KeywordDTO> keywords;

    public KeywordsDTO(
            @JsonProperty(value = "keywords", required = true) @NonNull List<KeywordDTO> keywords)
    {
        this.keywords = Collections.unmodifiableList(keywords);
    }

    @NonNull public List<KeywordDTO> getKeywords()
    {
        return keywords;
    }
}
