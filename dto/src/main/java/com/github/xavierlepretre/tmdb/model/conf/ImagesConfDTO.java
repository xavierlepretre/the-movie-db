package com.github.xavierlepretre.tmdb.model.conf;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class ImagesConfDTO
{
    @NonNull private final String baseUrl;
    @NonNull private final String secureBaseUrl;
    @NonNull private final List<ImageSizeDTO> backdropSizes;
    @NonNull private final List<ImageSizeDTO> logoSizes;
    @NonNull private final List<ImageSizeDTO> posterSizes;
    @NonNull private final List<ImageSizeDTO> profileSizes;
    @NonNull private final List<ImageSizeDTO> stillSizes;

    public ImagesConfDTO(
            @JsonProperty(value = "base_url", required = true) @NonNull
            String baseUrl,
            @JsonProperty(value = "secure_base_url", required = true) @NonNull
            String secureBaseUrl,
            @JsonProperty(value = "backdrop_sizes", required = true) @NonNull
            List<ImageSizeDTO> backdropSizes,
            @JsonProperty(value = "logo_sizes", required = true) @NonNull
            List<ImageSizeDTO> logoSizes,
            @JsonProperty(value = "poster_sizes", required = true) @NonNull
            List<ImageSizeDTO> posterSizes,
            @JsonProperty(value = "profile_sizes", required = true) @NonNull
            List<ImageSizeDTO> profileSizes,
            @JsonProperty(value = "still_sizes", required = true) @NonNull
            List<ImageSizeDTO> stillSizes)
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

    @NonNull public List<ImageSizeDTO> getBackdropSizes()
    {
        return backdropSizes;
    }

    @NonNull public List<ImageSizeDTO> getLogoSizes()
    {
        return logoSizes;
    }

    @NonNull public List<ImageSizeDTO> getPosterSizes()
    {
        return posterSizes;
    }

    @NonNull public List<ImageSizeDTO> getProfileSizes()
    {
        return profileSizes;
    }

    @NonNull public List<ImageSizeDTO> getStillSizes()
    {
        return stillSizes;
    }
}
