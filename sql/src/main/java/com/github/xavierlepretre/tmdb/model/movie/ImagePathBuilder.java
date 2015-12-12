package com.github.xavierlepretre.tmdb.model.movie;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.conf.Configuration;
import com.github.xavierlepretre.tmdb.model.conf.ImageSize;
import com.github.xavierlepretre.tmdb.model.image.ImagePath;

public class ImagePathBuilder
{
    @NonNull public String getUrl(
            @NonNull Configuration configuration,
            @NonNull ImageSize imageSize,
            @NonNull ImagePath imagePath)
    {
        if (configuration.getImagesConf().getBaseUrl() == null)
        {
            throw new NullPointerException("BaseUrl cannot be null");
        }
        //noinspection StringBufferReplaceableByString
        return new StringBuilder(configuration.getImagesConf().getBaseUrl())
                .append(imageSize.getSize())
                .append(imagePath.getPath())
                .toString();
    }

    @NonNull public String getSecureUrl(
            @NonNull Configuration configuration,
            @NonNull ImageSize imageSize,
            @NonNull ImagePath imagePath)
    {
        if (configuration.getImagesConf().getSecureBaseUrl() == null)
        {
            throw new NullPointerException("SecureBaseUrl cannot be null");
        }
        //noinspection StringBufferReplaceableByString
        return new StringBuilder(configuration.getImagesConf().getSecureBaseUrl())
                .append(imageSize.getSize())
                .append(imagePath.getPath())
                .toString();
    }
}
