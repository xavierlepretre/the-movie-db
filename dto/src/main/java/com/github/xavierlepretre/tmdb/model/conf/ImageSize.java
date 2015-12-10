package com.github.xavierlepretre.tmdb.model.conf;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Image size as can be passed as part of the path of images.
 */
public class ImageSize
{
    @NonNull private final String size;

    @JsonCreator
    public ImageSize(@NonNull String size)
    {
        this.size = size;
    }

    @NonNull @JsonValue
    public String getSize()
    {
        return size;
    }

    @Override public int hashCode()
    {
        return size.hashCode();
    }

    @Override public boolean equals(Object obj)
    {
        return obj instanceof ImageSize
                && ((ImageSize) obj).getSize().equals(size);
    }

    @NonNull public Integer getWidth()
    {
        return  Integer.parseInt(size.replaceFirst("w(\\d+)", "$1"));
    }
}
