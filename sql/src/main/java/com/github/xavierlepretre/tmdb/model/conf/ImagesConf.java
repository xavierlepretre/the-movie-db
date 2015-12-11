package com.github.xavierlepretre.tmdb.model.conf;

import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;

public class ImagesConf
{
    @Nullable private final String baseUrl;
    @Nullable private final String secureBaseUrl;
    @Nullable private final List<ImageSize> backdropSizes;
    @Nullable private final List<ImageSize> logoSizes;
    @Nullable private final List<ImageSize> posterSizes;
    @Nullable private final List<ImageSize> profileSizes;
    @Nullable private final List<ImageSize> stillSizes;

    public ImagesConf(
            @Nullable String baseUrl,
            @Nullable String secureBaseUrl,
            @Nullable List<ImageSize> backdropSizes,
            @Nullable List<ImageSize> logoSizes,
            @Nullable List<ImageSize> posterSizes,
            @Nullable List<ImageSize> profileSizes,
            @Nullable List<ImageSize> stillSizes)
    {
        this.baseUrl = baseUrl;
        this.secureBaseUrl = secureBaseUrl;
        this.backdropSizes = backdropSizes == null ? null : Collections.unmodifiableList(backdropSizes);
        this.logoSizes = logoSizes == null ? null : Collections.unmodifiableList(logoSizes);
        this.posterSizes = posterSizes == null ? null : Collections.unmodifiableList(posterSizes);
        this.profileSizes = profileSizes == null ? null : Collections.unmodifiableList(profileSizes);
        this.stillSizes = stillSizes == null ? null : Collections.unmodifiableList(stillSizes);
    }

    @Nullable public String getBaseUrl()
    {
        return baseUrl;
    }

    @Nullable public String getSecureBaseUrl()
    {
        return secureBaseUrl;
    }

    @Nullable public List<ImageSize> getBackdropSizes()
    {
        return backdropSizes;
    }

    @Nullable public List<ImageSize> getLogoSizes()
    {
        return logoSizes;
    }

    @Nullable public List<ImageSize> getPosterSizes()
    {
        return posterSizes;
    }

    @Nullable public List<ImageSize> getProfileSizes()
    {
        return profileSizes;
    }

    @Nullable public List<ImageSize> getStillSizes()
    {
        return stillSizes;
    }
}
