package com.github.xavierlepretre.tmdb.model.image;

import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.conf.Configuration;
import com.github.xavierlepretre.tmdb.model.conf.ImagesConf;

public class ImagePathBuilder
{
    @NonNull public String getUrl(
            @NonNull Configuration configuration,
            @NonNull ImageSize imageSize,
            @NonNull ImagePath imagePath)
    {
        return getUrl(configuration.getImagesConf(), imageSize, imagePath);
    }

    @NonNull public String getUrl(
            @NonNull ImagesConf imagesConf,
            @NonNull ImageSize imageSize,
            @NonNull ImagePath imagePath)
    {
        if (imagesConf.getBaseUrl() == null)
        {
            throw new NullPointerException("BaseUrl cannot be null");
        }
        //noinspection StringBufferReplaceableByString
        return getUrl(imagesConf.getBaseUrl(),
                imageSize,
                imagePath);
    }

    @NonNull public String getUrl(
            @NonNull String baseUrl,
            @NonNull ImageSize imageSize,
            @NonNull ImagePath imagePath)
    {
        //noinspection StringBufferReplaceableByString
        return new StringBuilder(baseUrl)
                .append(imageSize.getSize())
                .append(imagePath.getPath())
                .toString();
    }

    @NonNull public String getSecureUrl(
            @NonNull Configuration configuration,
            @NonNull ImageSize imageSize,
            @NonNull ImagePath imagePath)
    {
        return getSecureUrl(configuration.getImagesConf(), imageSize, imagePath);
    }

    @NonNull public String getSecureUrl(
            @NonNull ImagesConf imagesConf,
            @NonNull ImageSize imageSize,
            @NonNull ImagePath imagePath)
    {
        if (imagesConf.getSecureBaseUrl() == null)
        {
            throw new NullPointerException("SecureBaseUrl cannot be null");
        }
        //noinspection StringBufferReplaceableByString
        return getSecureUrl(
                imagesConf.getSecureBaseUrl(),
                imageSize,
                imagePath);
    }

    @NonNull public String getSecureUrl(
            @NonNull String secureBaseUrl,
            @NonNull ImageSize imageSize,
            @NonNull ImagePath imagePath)
    {
        //noinspection StringBufferReplaceableByString
        return new StringBuilder(secureBaseUrl)
                .append(imageSize.getSize())
                .append(imagePath.getPath())
                .toString();
    }
}
