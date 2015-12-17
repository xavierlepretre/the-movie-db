package com.github.xavierlepretre.tmdb.sql.conf;

import android.database.MatrixCursor;

import com.github.xavierlepretre.tmdb.model.conf.ChangeKey;
import com.github.xavierlepretre.tmdb.model.conf.Configuration;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract.ImagesConfSegment;
import com.github.xavierlepretre.tmdb.model.conf.ImageSize;
import com.github.xavierlepretre.tmdb.sql.conf.ConfigurationCursor;

import org.junit.Test;

import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;

public class ConfigurationCursorTest
{
    private static final String[] COLUMNS = new String[]{
            ImagesConfSegment.COLUMN_BASE_URL,
            ImagesConfSegment.COLUMN_SECURE_BASE_URL,
            ImagesConfSegment.COLUMN_BACKDROP_SIZES,
            ImagesConfSegment.COLUMN_LOGO_SIZES,
            ImagesConfSegment.COLUMN_POSTER_SIZES,
            ImagesConfSegment.COLUMN_PROFILE_SIZES,
            ImagesConfSegment.COLUMN_STILL_SIZES,
            ConfigurationContract.COLUMN_CHANGE_KEYS
    };
    private static final String[] VALUES = new String[]{
            "baseUrl",
            "secureBaseUrl",
            "w1,w2",
            "w3,w4",
            "w5,w6",
            "w7,w8",
            "w9,w10",
            "key11,key12"
    };

    @Test
    public void mayCreateConfiguration() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(VALUES);
        ConfigurationCursor entityCursor = new ConfigurationCursor(cursor);
        entityCursor.moveToFirst();

        Configuration configuration = entityCursor.getConfiguration();
        assertThat(configuration.getImagesConf().getBaseUrl()).isEqualTo("baseUrl");
        assertThat(configuration.getImagesConf().getSecureBaseUrl()).isEqualTo("secureBaseUrl");
        assertThat(configuration.getImagesConf().getBackdropSizes()).isEqualTo(
                Arrays.asList(new ImageSize("w1"), new ImageSize("w2")));
        assertThat(configuration.getImagesConf().getLogoSizes()).isEqualTo(
                Arrays.asList(new ImageSize("w3"), new ImageSize("w4")));
        assertThat(configuration.getImagesConf().getPosterSizes()).isEqualTo(
                Arrays.asList(new ImageSize("w5"), new ImageSize("w6")));
        assertThat(configuration.getImagesConf().getProfileSizes()).isEqualTo(
                Arrays.asList(new ImageSize("w7"), new ImageSize("w8")));
        assertThat(configuration.getImagesConf().getStillSizes()).isEqualTo(
                Arrays.asList(new ImageSize("w9"), new ImageSize("w10")));
        assertThat(configuration.getChangeKeys()).isEqualTo(
                Arrays.asList(new ChangeKey("key11"), new ChangeKey("key12")));
    }

    @Test
    public void mayCreateConfigurationWithMissing() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(new String[0]);
        cursor.addRow(new String[0]);
        ConfigurationCursor entityCursor = new ConfigurationCursor(cursor);
        entityCursor.moveToFirst();

        Configuration configuration = entityCursor.getConfiguration();
        assertThat(configuration.getImagesConf().getBaseUrl()).isNull();
        assertThat(configuration.getImagesConf().getSecureBaseUrl()).isNull();
        assertThat(configuration.getImagesConf().getBackdropSizes()).isNull();
        assertThat(configuration.getImagesConf().getLogoSizes()).isNull();
        assertThat(configuration.getImagesConf().getPosterSizes()).isNull();
        assertThat(configuration.getImagesConf().getProfileSizes()).isNull();
        assertThat(configuration.getImagesConf().getStillSizes()).isNull();
        assertThat(configuration.getChangeKeys()).isNull();
    }

    @Test
    public void mayCreateConfigurationWithNulls() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        cursor.addRow(new String[COLUMNS.length]);
        ConfigurationCursor entityCursor = new ConfigurationCursor(cursor);
        entityCursor.moveToFirst();

        Configuration configuration = entityCursor.getConfiguration();
        assertThat(configuration.getImagesConf().getBaseUrl()).isNull();
        assertThat(configuration.getImagesConf().getSecureBaseUrl()).isNull();
        assertThat(configuration.getImagesConf().getBackdropSizes()).isNull();
        assertThat(configuration.getImagesConf().getLogoSizes()).isNull();
        assertThat(configuration.getImagesConf().getPosterSizes()).isNull();
        assertThat(configuration.getImagesConf().getProfileSizes()).isNull();
        assertThat(configuration.getImagesConf().getStillSizes()).isNull();
        assertThat(configuration.getChangeKeys()).isNull();
    }
}