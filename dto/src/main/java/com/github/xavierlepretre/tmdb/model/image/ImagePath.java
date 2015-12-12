package com.github.xavierlepretre.tmdb.model.image;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Encapsulates a string that is the end part of an image path.
 */
public class ImagePath
{
    @NonNull private final String path;

    @JsonCreator
    public ImagePath(@NonNull String path)
    {
        this.path = path;
    }

    @JsonValue @NonNull public String getPath()
    {
        return path;
    }

    @Override public final int hashCode()
    {
        return path.hashCode();
    }

    @Override public final boolean equals(Object obj)
    {
        return obj instanceof ImagePath
                && ((ImagePath) obj).getPath().equals(path);
    }

    @Override public String toString()
    {
        return "ImagePath{" +
                "path='" + path + '\'' +
                '}';
    }
}
