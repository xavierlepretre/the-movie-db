package com.github.xavierlepretre.tmdb.model.conf;

import android.database.MatrixCursor;
import android.support.annotation.NonNull;

import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract.ImagesConfSegment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
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

    private static class Parameter
    {
        @NonNull private final List<String> columns;
        @NonNull private final List<String> values;

        public Parameter(@NonNull List<String> columns,
                         @NonNull List<String> values)
        {
            this.columns = columns;
            this.values = values;
        }
    }

    @Parameterized.Parameter
    public Parameter parameter;

    @Parameterized.Parameters()
    public static Parameter[] getParameters()
    {
        int maxVal = 1 << POTENTIAL_COLUMNS.length;
        Parameter[] parameters = new Parameter[maxVal];

        // Picking which columns in a binary way
        for (int i = 0; i < maxVal; i++)
        {
            List<String> columns = new ArrayList<>();
            List<String> values = new ArrayList<>();
            for (int j = 0; j < POTENTIAL_COLUMNS.length; j++)
            {
                if ((i & (1 << j)) >= 1)
                {
                    columns.add(POTENTIAL_COLUMNS[j][0]);
                    values.add(POTENTIAL_COLUMNS[j][1]);
                }
            }
            parameters[i] = new Parameter(columns, values);
        }
        return parameters;
    }


    @Test
    public void testSometimesNull() throws Exception
    {
        MatrixCursor cursor = new MatrixCursor(parameter.columns.toArray(new String[parameter.columns.size()]));
        cursor.addRow(parameter.values);
        ConfigurationCursor configurationCursor = new ConfigurationCursor(cursor);
        configurationCursor.moveToFirst();

        Configuration configuration = configurationCursor.getConfiguration();
        if (parameter.columns.contains(ImagesConfSegment.COLUMN_BASE_URL))
        {
            assertThat(configuration.getImagesConf().getBaseUrl()).isEqualTo("baseUrl");
        }
        else
        {
            assertThat(configuration.getImagesConf().getBaseUrl()).isNull();
        }
        if (parameter.columns.contains(ImagesConfSegment.COLUMN_SECURE_BASE_URL))
        {
            assertThat(configuration.getImagesConf().getSecureBaseUrl()).isEqualTo("secureBaseUrl");
        }
        else
        {
            assertThat(configuration.getImagesConf().getSecureBaseUrl()).isNull();
        }

        if (parameter.columns.contains(ImagesConfSegment.COLUMN_BACKDROP_SIZES))
        {
            assertThat(configuration.getImagesConf().getBackdropSizes())
                    .isEqualTo(Arrays.asList(new ImageSize("w1"), new ImageSize("w2")));
        }
        else
        {
            assertThat(configuration.getImagesConf().getBackdropSizes()).isNull();
        }
        if (parameter.columns.contains(ImagesConfSegment.COLUMN_LOGO_SIZES))
        {
            assertThat(configuration.getImagesConf().getLogoSizes())
                    .isEqualTo(Arrays.asList(new ImageSize("w3"), new ImageSize("w4")));
        }
        else
        {
            assertThat(configuration.getImagesConf().getLogoSizes()).isNull();
        }
        if (parameter.columns.contains(ImagesConfSegment.COLUMN_POSTER_SIZES))
        {
            assertThat(configuration.getImagesConf().getPosterSizes())
                    .isEqualTo(Arrays.asList(new ImageSize("w5"), new ImageSize("w6")));
        }
        else
        {
            assertThat(configuration.getImagesConf().getPosterSizes()).isNull();
        }
        if (parameter.columns.contains(ImagesConfSegment.COLUMN_PROFILE_SIZES))
        {
            assertThat(configuration.getImagesConf().getProfileSizes())
                    .isEqualTo(Arrays.asList(new ImageSize("w7"), new ImageSize("w8")));
        }
        else
        {
            assertThat(configuration.getImagesConf().getProfileSizes()).isNull();
        }
        if (parameter.columns.contains(ImagesConfSegment.COLUMN_STILL_SIZES))
        {
            assertThat(configuration.getImagesConf().getStillSizes())
                    .isEqualTo(Arrays.asList(new ImageSize("w9"), new ImageSize("w10")));
        }
        else
        {
            assertThat(configuration.getImagesConf().getStillSizes()).isNull();
        }
        if (parameter.columns.contains(ConfigurationContract.COLUMN_CHANGE_KEYS))
        {
            assertThat(configuration.getChangeKeys())
                    .isEqualTo(Arrays.asList(new ChangeKey("key11"), new ChangeKey("key12")));
        }
        else
        {
            assertThat(configuration.getChangeKeys()).isNull();
        }
    }
}