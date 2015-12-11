package com.github.xavierlepretre.tmdb.model.conf;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract.ImagesConfSegment;

public class ContentValuesConfigurationFactory
{
    @NonNull public ContentValues createFrom(@NonNull ConfigurationDTO configurationDTO)
    {
        ContentValues contentValues = new ContentValues();
        populate(contentValues, configurationDTO);
        return contentValues;
    }

    public void populate(
            @NonNull ContentValues contentValues, @NonNull ConfigurationDTO configurationDTO)
    {
        contentValues.put(ConfigurationContract._ID, ConfigurationContract.UNIQUE_ROW_ID);
        contentValues.put(ImagesConfSegment.COLUMN_BASE_URL, configurationDTO.getImagesConf().getBaseUrl());
        contentValues.put(ImagesConfSegment.COLUMN_SECURE_BASE_URL, configurationDTO.getImagesConf().getSecureBaseUrl());
        contentValues.put(
                ImagesConfSegment.COLUMN_BACKDROP_SIZES,
                new ImageSizeList(configurationDTO.getImagesConf().getBackdropSizes()).join());
        contentValues.put(
                ImagesConfSegment.COLUMN_LOGO_SIZES,
                new ImageSizeList(configurationDTO.getImagesConf().getLogoSizes()).join());
        contentValues.put(
                ImagesConfSegment.COLUMN_POSTER_SIZES,
                new ImageSizeList(configurationDTO.getImagesConf().getPosterSizes()).join());
        contentValues.put(
                ImagesConfSegment.COLUMN_PROFILE_SIZES,
                new ImageSizeList(configurationDTO.getImagesConf().getProfileSizes()).join());
        contentValues.put(
                ImagesConfSegment.COLUMN_STILL_SIZES,
                new ImageSizeList(configurationDTO.getImagesConf().getStillSizes()).join());
        contentValues.put(
                ConfigurationContract.COLUMN_CHANGE_KEYS,
                new ChangeKeyList(configurationDTO.getChangeKeys()).join());
    }
}
