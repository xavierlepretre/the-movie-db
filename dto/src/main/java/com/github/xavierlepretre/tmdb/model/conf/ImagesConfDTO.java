package com.github.xavierlepretre.tmdb.model.conf;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.xavierlepretre.tmdb.model.image.ImageSize;

import java.util.Collections;
import java.util.List;

public class ImagesConfDTO
{
    @NonNull private final String baseUrl;
    @NonNull private final String secureBaseUrl;
    @NonNull private final List<ImageSize> backdropSizes;
    @NonNull private final List<ImageSize> logoSizes;
    @NonNull private final List<ImageSize> posterSizes;
    @NonNull private final List<ImageSize> profileSizes;
    @NonNull private final List<ImageSize> stillSizes;

    public ImagesConfDTO(
            @JsonProperty(value = "base_url", required = true) @NonNull
            String baseUrl,
            @JsonProperty(value = "secure_base_url", required = true) @NonNull
            String secureBaseUrl,
            @JsonProperty(value = "backdrop_sizes", required = true) @NonNull
            List<ImageSize> backdropSizes,
            @JsonProperty(value = "logo_sizes", required = true) @NonNull
            List<ImageSize> logoSizes,
            @JsonProperty(value = "poster_sizes", required = true) @NonNull
            List<ImageSize> posterSizes,
            @JsonProperty(value = "profile_sizes", required = true) @NonNull
            List<ImageSize> profileSizes,
            @JsonProperty(value = "still_sizes", required = true) @NonNull
            List<ImageSize> stillSizes)
    {
        this.baseUrl = baseUrl;
        this.secureBaseUrl = secureBaseUrl;
        this.backdropSizes = Collections.unmodifiableList(backdropSizes);
        this.logoSizes = Collections.unmodifiableList(logoSizes);
        this.posterSizes = Collections.unmodifiableList(posterSizes);
        this.profileSizes = Collections.unmodifiableList(profileSizes);
        this.stillSizes = Collections.unmodifiableList(stillSizes);
    }

    @NonNull public String getBaseUrl()
    {
        return baseUrl;
    }

    @NonNull public String getSecureBaseUrl()
    {
        return secureBaseUrl;
    }

    @NonNull public List<ImageSize> getBackdropSizes()
    {
        return backdropSizes;
    }

    @NonNull public List<ImageSize> getLogoSizes()
    {
        return logoSizes;
    }

    @NonNull public List<ImageSize> getPosterSizes()
    {
        return posterSizes;
    }

    @NonNull public List<ImageSize> getProfileSizes()
    {
        return profileSizes;
    }

    @NonNull public List<ImageSize> getStillSizes()
    {
        return stillSizes;
    }
}
