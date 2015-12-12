package com.github.xavierlepretre.tmdb.model.image;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

public class ImageDTO
{
    private final float aspectRatio;
    @NonNull private final ImagePath filePath;
    private final int height;
    @Nullable private final Locale iso639Dash1;
    private final float voteAverage;
    private final int voteCount;
    private final int width;

    public ImageDTO(@JsonProperty(value = "aspect_ratio", required = true) float aspectRatio,
                    @JsonProperty(value = "file_path", required = true) @NonNull ImagePath filePath,
                    @JsonProperty(value = "height", required = true) int height,
                    @JsonProperty(value = "iso_639_1", required = true) @Nullable Locale iso639Dash1,
                    @JsonProperty(value = "vote_average", required = true) float voteAverage,
                    @JsonProperty(value = "vote_count", required = true) int voteCount,
                    @JsonProperty(value = "width", required = true) int width)
    {
        this.aspectRatio = aspectRatio;
        this.filePath = filePath;
        this.height = height;
        this.iso639Dash1 = iso639Dash1;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.width = width;
    }

    public float getAspectRatio()
    {
        return aspectRatio;
    }

    @NonNull public ImagePath getFilePath()
    {
        return filePath;
    }

    public int getHeight()
    {
        return height;
    }

    @Nullable public Locale getIso639Dash1()
    {
        return iso639Dash1;
    }

    public float getVoteAverage()
    {
        return voteAverage;
    }

    public int getVoteCount()
    {
        return voteCount;
    }

    public int getWidth()
    {
        return width;
    }
}
