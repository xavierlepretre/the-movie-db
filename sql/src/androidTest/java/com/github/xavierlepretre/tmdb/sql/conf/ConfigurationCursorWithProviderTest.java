package com.github.xavierlepretre.tmdb.sql.conf;

import android.content.ContentValues;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import com.github.xavierlepretre.tmdb.model.conf.ChangeKey;
import com.github.xavierlepretre.tmdb.model.conf.Configuration;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract;
import com.github.xavierlepretre.tmdb.model.conf.ConfigurationContract.ImagesConfSegment;
import com.github.xavierlepretre.tmdb.model.image.ImageSize;
import com.github.xavierlepretre.tmdb.sql.EntitySQLiteOpenHelper;
import com.github.xavierlepretre.tmdb.sql.ParameterColumnValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(Parameterized.class)
public class ConfigurationCursorWithProviderTest
{
    private static final String TEMP_DB_NAME = "temp.configuration.db";
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

    private ConfigurationProviderDelegate providerDelegate;
    private EntitySQLiteOpenHelper sqlHelper;

    @Parameterized.Parameter
    public ParameterColumnValue parameter;

    @Parameterized.Parameters()
    public static ParameterColumnValue[] getParameters()
    {
        return ParameterColumnValue.getPossibleParameters(POTENTIAL_COLUMNS);
    }

    @Before
    public void setUp() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
        providerDelegate = spy(new ConfigurationProviderDelegate(
                "content_authority",
                Uri.parse("content://content_authority/configuration"),
                "item_type"));
        sqlHelper = new EntitySQLiteOpenHelper(
                InstrumentationRegistry.getContext(),
                TEMP_DB_NAME,
                null,
                1,
                new ConfigurationSQLHelperDelegate());

        ContentValues values = new ContentValues();
        for (String[] pair : POTENTIAL_COLUMNS)
        {
            values.put(pair[0], pair[1]);
        }
        providerDelegate.insert(
                sqlHelper.getWritableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                values);
    }

    @After
    public void tearDown() throws Exception
    {
        InstrumentationRegistry.getContext().deleteDatabase(TEMP_DB_NAME);
    }

    @Test
    public void queryListFromDb_isOk() throws Exception
    {
        //noinspection ConstantConditions
        ConfigurationCursor found = new ConfigurationCursor(providerDelegate.query(
                sqlHelper.getReadableDatabase(),
                Uri.parse("content://content_authority/configuration"),
                parameter.columns.toArray(new String[parameter.columns.size()]),
                null, null, null, null,
                ConfigurationContract._ID + " ASC", null));

        assertThat(found).isNotNull();
        //noinspection ConstantConditions
        assertThat(found.getCount()).isEqualTo(1);
        assertThat(found.moveToFirst()).isTrue();
        Configuration configuration = found.getConfiguration();
        assertThat(configuration.getImagesConf().getBaseUrl()).isEqualTo(
                (parameter.columns.contains(ImagesConfSegment.COLUMN_BASE_URL) || parameter.columns.size() == 0)
                        ? "baseUrl"
                        : null);
        assertThat(configuration.getImagesConf().getSecureBaseUrl()).isEqualTo(
                (parameter.columns.contains(ImagesConfSegment.COLUMN_SECURE_BASE_URL) || parameter.columns.size() == 0)
                        ? "secureBaseUrl"
                        : null);
        assertThat(configuration.getImagesConf().getBackdropSizes()).isEqualTo(
                (parameter.columns.contains(ImagesConfSegment.COLUMN_BACKDROP_SIZES) || parameter.columns.size() == 0)
                        ? Arrays.asList(new ImageSize("w1"), new ImageSize("w2"))
                        : null);
        assertThat(configuration.getImagesConf().getLogoSizes()).isEqualTo(
                (parameter.columns.contains(ImagesConfSegment.COLUMN_LOGO_SIZES) || parameter.columns.size() == 0)
                        ? Arrays.asList(new ImageSize("w3"), new ImageSize("w4"))
                        : null);
        assertThat(configuration.getImagesConf().getPosterSizes()).isEqualTo(
                (parameter.columns.contains(ImagesConfSegment.COLUMN_POSTER_SIZES) || parameter.columns.size() == 0)
                        ? Arrays.asList(new ImageSize("w5"), new ImageSize("w6"))
                        : null);
        assertThat(configuration.getImagesConf().getProfileSizes()).isEqualTo(
                (parameter.columns.contains(ImagesConfSegment.COLUMN_PROFILE_SIZES) || parameter.columns.size() == 0)
                        ? Arrays.asList(new ImageSize("w7"), new ImageSize("w8"))
                        : null);
        assertThat(configuration.getImagesConf().getStillSizes()).isEqualTo(
                (parameter.columns.contains(ImagesConfSegment.COLUMN_STILL_SIZES) || parameter.columns.size() == 0)
                        ? Arrays.asList(new ImageSize("w9"), new ImageSize("w10"))
                        : null);
        assertThat(configuration.getChangeKeys()).isEqualTo(
                (parameter.columns.contains(ConfigurationContract.COLUMN_CHANGE_KEYS) || parameter.columns.size() == 0)
                        ? Arrays.asList(new ChangeKey("key11"), new ChangeKey("key12"))
                        : null);
        assertThat(found.moveToNext()).isFalse();
    }
}
