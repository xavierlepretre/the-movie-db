package com.github.xavierlepretre.tmdb.model.conf;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class ConfigurationDTO
{
    @NonNull private final ImagesConfDTO imagesConf;
    @NonNull private final List<ChangeKeyDTO> changeKeys;

    public ConfigurationDTO(
            @JsonProperty(value = "images", required = true) @NonNull
            ImagesConfDTO imagesConf,
            @JsonProperty(value = "change_keys", required = true) @NonNull
            List<ChangeKeyDTO> changeKeys)
    {
        this.imagesConf = imagesConf;
        this.changeKeys = Collections.unmodifiableList(changeKeys);
    }

    @NonNull public ImagesConfDTO getImagesConf()
    {
        return imagesConf;
    }

    @NonNull public List<ChangeKeyDTO> getChangeKeys()
    {
        return changeKeys;
    }
}
