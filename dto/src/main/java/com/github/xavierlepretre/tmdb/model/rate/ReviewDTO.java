package com.github.xavierlepretre.tmdb.model.rate;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReviewDTO
{
    @NonNull private final ReviewId id;
    @NonNull private final String author;
    @NonNull private final String content;
    @NonNull private final String url;

    public ReviewDTO(
            @JsonProperty(value = "id", required = true) @NonNull ReviewId id,
            @JsonProperty(value = "author", required = true) @NonNull String author,
            @JsonProperty(value = "content", required = true) @NonNull String content,
            @JsonProperty(value = "url", required = true) @NonNull String url)
    {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    @NonNull public ReviewId getId()
    {
        return id;
    }

    @NonNull public String getAuthor()
    {
        return author;
    }

    @NonNull public String getContent()
    {
        return content;
    }

    @NonNull public String getUrl()
    {
        return url;
    }
}
