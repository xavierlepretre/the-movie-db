package com.github.xavierlepretre.tmdb.model.conf;

import android.database.MatrixCursor;

import com.github.xavierlepretre.tmdb.model.ParameterColumnValue;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract.ImagesConfSegment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ConfigurationCursorTest
{
    private static final String[][] POTENTIAL_COLUMNS = new String[][]{
            new String[]{ImagesConfSegment.COLUMN_BASE_URL, "baseUrl"},
            new String[]{ImagesConfSegment.COLUMN_SECURE_BASE_URL, "secureBaseUrl"},
            new String[]{ImagesConfSegment.COLUMN_BACKDROP_SIZES, "w1,w2"},
            new String[]{ImagesConfSegment.COLUMN_LOGO_SIZES, "w3,w4"},
            new String[]{ImagesConfSegment.COLUMN_POSTER_SIZES, "w5,w6"},
            new String[]{ImagesConfSegment.COLUMN_PROFILE_SIZES, "w7,w8"},
            new String[]{ImagesConfSegment.COLUMN_STILL_SIZES, "w9,w10"},
            new String[]{ConfigurationContract.COLUMN_CHANGE_KEYS, "key11,key12"}
    };

    @Parameterized.Parameter
    public ParameterColumnValue parameter;

    @Parameterized.Parameters()
    public static ParameterColumnValue[] getParameters()
    {
        return ParameterColumnValue.getPossibleParameters(POTENTIAL_COLUMNS);
    }

    @Test
    public void testSometimesNull() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(parameter.columns.toArray(new String[parameter.columns.size()]));
        for (List<String> row : parameter.rows)
        {
            cursor.addRow(row);
        }
        ConfigurationCursor entityCursor = new ConfigurationCursor(cursor);
        entityCursor.moveToFirst();

        Configuration configuration = entityCursor.getConfiguration();
        assertThat(configuration.getImagesConf().getBaseUrl()).isEqualTo(
                parameter.columns.contains(ImagesConfSegment.COLUMN_BASE_URL)
                        ? "baseUrl"
                        : null);
        assertThat(configuration.getImagesConf().getSecureBaseUrl()).isEqualTo(
                parameter.columns.contains(ImagesConfSegment.COLUMN_SECURE_BASE_URL)
                        ? "secureBaseUrl"
                        : null);
        assertThat(configuration.getImagesConf().getBackdropSizes()).isEqualTo(
                parameter.columns.contains(ImagesConfSegment.COLUMN_BACKDROP_SIZES)
                        ? Arrays.asList(new ImageSize("w1"), new ImageSize("w2"))
                        : null);
        assertThat(configuration.getImagesConf().getLogoSizes()).isEqualTo(
                parameter.columns.contains(ImagesConfSegment.COLUMN_LOGO_SIZES)
                        ? Arrays.asList(new ImageSize("w3"), new ImageSize("w4"))
                        : null);
        assertThat(configuration.getImagesConf().getPosterSizes()).isEqualTo(
                parameter.columns.contains(ImagesConfSegment.COLUMN_POSTER_SIZES)
                        ? Arrays.asList(new ImageSize("w5"), new ImageSize("w6"))
                        : null);
        assertThat(configuration.getImagesConf().getProfileSizes()).isEqualTo(
                parameter.columns.contains(ImagesConfSegment.COLUMN_PROFILE_SIZES)
                        ? Arrays.asList(new ImageSize("w7"), new ImageSize("w8"))
                        : null);
        assertThat(configuration.getImagesConf().getStillSizes()).isEqualTo(
                parameter.columns.contains(ImagesConfSegment.COLUMN_STILL_SIZES)
                        ? Arrays.asList(new ImageSize("w9"), new ImageSize("w10"))
                        : null);
        assertThat(configuration.getChangeKeys()).isEqualTo(
                parameter.columns.contains(ConfigurationContract.COLUMN_CHANGE_KEYS)
                        ? Arrays.asList(new ChangeKey("key11"), new ChangeKey("key12"))
                        : null);
    }
}