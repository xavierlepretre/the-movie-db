package com.github.xavierlepretre.tmdb.model.tag;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.image.HasPosterPathDTO;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;
import com.neovisionaries.i18n.LanguageCode;

public class ListDTO implements HasPosterPathDTO
{
    @NonNull private final String description;
    private final int favoriteCount;
    @NonNull private final ListId id;
    @NonNull private final LanguageCode iso639Dash1;
    private final int itemCount;
    @NonNull private final String name;
    @NonNull private final ImagePath posterPath;

    public ListDTO(
            @JsonProperty(value = "description", required = true) @NonNull String description,
            @JsonProperty(value = "favorite_count", required = true) int favoriteCount,
            @JsonProperty(value = "id", required = true) @NonNull ListId id,
            @JsonProperty(value = "iso_639_1", required = true) @NonNull LanguageCode iso639Dash1,
            @JsonProperty(value = "item_count", required = true) int itemCount,
            @JsonProperty(value = "name", required = true) @NonNull String name,
            @JsonProperty(value = "poster_path", required = true) @NonNull ImagePath posterPath)
    {
        this.description = description;
        this.favoriteCount = favoriteCount;
        this.id = id;
        this.iso639Dash1 = iso639Dash1;
        this.itemCount = itemCount;
        this.name = name;
        this.posterPath = posterPath;
    }

    @NonNull public String getDescription()
    {
        return description;
    }

    public int getFavoriteCount()
    {
        return favoriteCount;
    }

    @NonNull public ListId getId()
    {
        return id;
    }

    @NonNull public LanguageCode getIso639Dash1()
    {
        return iso639Dash1;
    }

    public int getItemCount()
    {
        return itemCount;
    }

    @NonNull public String getName()
    {
        return name;
    }

    @NonNull @Override public ImagePath getPosterPath()
    {
        return posterPath;
    }
}
