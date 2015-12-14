package com.github.xavierlepretre.tmdb.model.show;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.LanguageCode;

public class VideoDTO
{
    @NonNull private final VideoId id;
    @NonNull private final LanguageCode iso639Dash1;
    @NonNull private final String key;
    @NonNull private final String name;
    @NonNull private final String site;
    private final int size;
    @NonNull private final String type;

    public VideoDTO(
            @JsonProperty(value = "id", required = true) @NonNull VideoId id,
            @JsonProperty(value = "iso_639_1", required = true) @NonNull LanguageCode iso639Dash1,
            @JsonProperty(value = "key", required = true) @NonNull String key,
            @JsonProperty(value = "name", required = true) @NonNull String name,
            @JsonProperty(value = "site", required = true) @NonNull String site,
            @JsonProperty(value = "size", required = true) int size,
            @JsonProperty(value = "type", required = true) @NonNull String type)
    {
        this.id = id;
        this.iso639Dash1 = iso639Dash1;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    @NonNull public VideoId getId()
    {
        return id;
    }

    @NonNull public LanguageCode getIso639Dash1()
    {
        return iso639Dash1;
    }

    @NonNull public String getKey()
    {
        return key;
    }

    @NonNull public String getName()
    {
        return name;
    }

    @NonNull public String getSite()
    {
        return site;
    }

    public int getSize()
    {
        return size;
    }

    @NonNull public String getType()
    {
        return type;
    }
}
