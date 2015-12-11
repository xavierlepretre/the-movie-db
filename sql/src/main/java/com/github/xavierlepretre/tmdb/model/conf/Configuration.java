package com.github.xavierlepretre.tmdb.model.conf;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;

public class Configuration
{
    @NonNull private final ImagesConf imagesConf;
    @Nullable private final List<ChangeKey> changeKeys;

    public Configuration(
            @NonNull ImagesConf imagesConf,
            @Nullable List<ChangeKey> changeKeys)
    {
        this.imagesConf = imagesConf;
        this.changeKeys = changeKeys == null ? null : Collections.unmodifiableList(changeKeys);
    }

    @NonNull public ImagesConf getImagesConf()
    {
        return imagesConf;
    }

    @Nullable public List<ChangeKey> getChangeKeys()
    {
        return changeKeys;
    }
}
