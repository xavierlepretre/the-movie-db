package com.github.xavierlepretre.tmdb.sql.conf;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.conf.ChangeKeyList;
import com.github.xavierlepretre.tmdb.model.conf.Configuration;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract.ImagesConfSegment;
import com.github.xavierlepretre.tmdb.model.conf.ImagesConf;
import com.github.xavierlepretre.tmdb.model.image.ImageSizeList;

public class ConfigurationCursor extends CursorWrapper
{
    private final int baseUrlCol;
    private final int secureBaseUrlCol;
    private final int backdropSizesCol;
    private final int logoSizesCol;
    private final int posterSizesCol;
    private final int profileSizesCol;
    private final int stillSizesCol;
    private final int changeKeysCol;

    public ConfigurationCursor(@NonNull Cursor cursor)
    {
        super(cursor);
        baseUrlCol = getColumnIndex(ImagesConfSegment.COLUMN_BASE_URL);
        secureBaseUrlCol = getColumnIndex(ImagesConfSegment.COLUMN_SECURE_BASE_URL);
        backdropSizesCol = getColumnIndex(ImagesConfSegment.COLUMN_BACKDROP_SIZES);
        logoSizesCol = getColumnIndex(ImagesConfSegment.COLUMN_LOGO_SIZES);
        posterSizesCol = getColumnIndex(ImagesConfSegment.COLUMN_POSTER_SIZES);
        profileSizesCol = getColumnIndex(ImagesConfSegment.COLUMN_PROFILE_SIZES);
        stillSizesCol = getColumnIndex(ImagesConfSegment.COLUMN_STILL_SIZES);
        changeKeysCol = getColumnIndex(ConfigurationContract.COLUMN_CHANGE_KEYS);
    }

    @NonNull public Configuration getConfiguration()
    {
        return new Configuration(
                new ImagesConf(
                        baseUrlCol < 0 ? null : getString(baseUrlCol),
                        secureBaseUrlCol < 0 ? null : getString(secureBaseUrlCol),
                        (backdropSizesCol < 0 || isNull(backdropSizesCol)) ? null : new ImageSizeList(getString(backdropSizesCol)),
                        (logoSizesCol < 0 || isNull(logoSizesCol)) ? null : new ImageSizeList(getString(logoSizesCol)),
                        (posterSizesCol < 0 || isNull(posterSizesCol)) ? null : new ImageSizeList(getString(posterSizesCol)),
                        (profileSizesCol < 0 || isNull(profileSizesCol)) ? null : new ImageSizeList(getString(profileSizesCol)),
                        (stillSizesCol < 0 || isNull(stillSizesCol)) ? null : new ImageSizeList(getString(stillSizesCol))),
                (changeKeysCol < 0 || isNull(changeKeysCol)) ? null : new ChangeKeyList(getString(changeKeysCol)));
    }
}
